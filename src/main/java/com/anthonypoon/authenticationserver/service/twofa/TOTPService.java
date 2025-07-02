package com.anthonypoon.authenticationserver.service.twofa;

import com.anthonypoon.authenticationserver.persistence.entity.totp.TOTPDeviceEntity;
import com.anthonypoon.authenticationserver.persistence.entity.totp.TOTPRecoveryEntry;
import com.anthonypoon.authenticationserver.persistence.repository.totp.TOTPDeviceEntityRepository;
import com.anthonypoon.authenticationserver.persistence.repository.totp.TOTPRecoveryEntityRepository;
import com.anthonypoon.authenticationserver.persistence.repository.user.ApplicationUserRepository;
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.service.encryption.EncryptionService;
import com.anthonypoon.authenticationserver.service.twofa.config.TwoFactorConfig;
import com.anthonypoon.authenticationserver.domains.twofa.TOTPDevice;
import com.anthonypoon.authenticationserver.service.twofa.exception.TOTPException;
import com.anthonypoon.authenticationserver.service.utils.RandomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TOTPService {
    private static final int TOTP_SECRET_LENGTH = 32;
    private static final int MAX_ITERATION = 10000;
    private final RandomService randoms;
    private final EncryptionService encryption;
    private final ApplicationUserRepository users;
    private final TOTPDeviceEntityRepository devices;
    private final TOTPRecoveryEntityRepository recoveries;
    private final TwoFactorConfig config;

    public TOTPService(
            RandomService randoms,
            EncryptionService encryption,
            ApplicationUserRepository users,
            TOTPDeviceEntityRepository devices,
            TOTPRecoveryEntityRepository recoveries,
            TwoFactorConfig config
    ) {
        this.randoms = randoms;
        this.encryption = encryption;
        this.users = users;
        this.devices = devices;
        this.recoveries = recoveries;
        this.config = config;
    }

    public TOTPDevice register(UserPrinciple user, String deviceName) {
        var secret = this.randoms.secure(TOTP_SECRET_LENGTH);
        var userEntity = this.users.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id"));
        var encrypted = this.encryption.encrypt(secret);
        var device = TOTPDeviceEntity.builder()
                .deviceName(deviceName)
                .secret(encrypted)
                .user(userEntity)
                .build();
        this.devices.save(device);
        return TOTPDevice.getInstance(device, config, secret);
    }

    public boolean validate(Long id, UserPrinciple user, String totp) {
        var device = this.devices.findByIdAndUser(id, user.getId())
                .orElse(null);
        if (device == null) {
            return false;
        }
        var compare = this.generateTOTP(encryption.decrypt(device.getSecret()));
        var isValid = compare.equals(totp);
        if (!isValid) {
            return false;
        }
        device.setValidated(true);
        this.devices.saveAndFlush(device);
        return true;
    }

    public List<TOTPDevice> getDevices(UserPrinciple user) {
        var devices = this.devices.findAllValidByUser(user.getId());
        return devices.stream()
                .map(device -> TOTPDevice.getInstance(device, this.config, this.encryption.decrypt(device.getSecret())))
                .collect(Collectors.toList());
    }

    public void unregister(UserPrinciple user, Long id) throws TOTPException {
        var devices = this.devices.findAllValidByUser(user.getId());
        if (devices.size() == 1) {
            throw new TOTPException("Cannot unregister the last device");
        }
        var device = devices.stream().filter(s -> s.getId().equals(id)).findFirst();
        if (device.isEmpty()) {
            throw new TOTPException("Device not found.");
        }
        this.devices.delete(device.get());
    }

    public List<String> getRecoveries(UserPrinciple user) {
        return this.recoveries.findAllValidByUser(user.getId()).stream()
                .map(r -> new String(this.encryption.decrypt(r.getValue())))
                .collect(Collectors.toList());
    }

    public List<String> generateRecoveries(UserPrinciple user) {
        var recoveries = this.createRecoveries();
        var userEntity = this.users.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id"));
        var entities = recoveries.stream().map(value -> TOTPRecoveryEntry.builder()
                .user(userEntity)
                .value(this.encryption.encrypt(value))
                .build()
        ).collect(Collectors.toList());
        this.recoveries.saveAll(entities);
        return recoveries;
    }

    public boolean checkToken(UserPrinciple user, String totp) {
        var devices = this.devices.findAllValidByUser(user.getId());
        String token;
        for (TOTPDeviceEntity device : devices) {
            token = this.generateTOTP(encryption.decrypt(device.getSecret()));
            if (token.equals(totp)) {
                return true;
            }
        }
        var recoveries = this.recoveries.findAllValidByUser(user.getId()).stream().toList();
        for (TOTPRecoveryEntry recovery : recoveries) {
            token = new String(this.encryption.decrypt(recovery.getValue()));
            if (token.equals(totp)) {
                recovery.setConsumedAt(Instant.now());
                this.recoveries.save(recovery);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public List<String> regenerateRecoveries(UserPrinciple user) {
        var existing = this.recoveries.findAllValidByUser(user.getId());
        for (TOTPRecoveryEntry recovery: existing) {
            recovery.setConsumedAt(Instant.now());
        }
        this.recoveries.saveAll(existing);
        return this.generateRecoveries(user);
    }

    private List<String> createRecoveries() {
        var rtn = new HashMap<String, String>();
        var i = 0;
        do {
            if (i > MAX_ITERATION) {
                throw new RuntimeException("Failed to generate recovery code. Max attempts exceeded");
            }
            i++;
            var num = this.randoms.nextInt((int) Math.pow(10, config.getTokenLength()));
            var str = String.format("%0" + config.getTokenLength() + "d", num);
            if (rtn.containsKey(str)) {
                continue;
            }
            rtn.put(str, str);
        } while (rtn.size() < config.getTotpRecoveryCount());
        return new ArrayList<>(rtn.values());
    }

    private String generateTOTP(byte[] device) {
        try {
            long step = System.currentTimeMillis() / 1000 / config.getTimeStep();
            byte[] data = longToBytes(step);

            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(device, config.getAlgo());
            mac.init(keySpec);

            byte[] hmac = mac.doFinal(data);
            int offset = hmac[hmac.length - 1] & 0xF;
            int binary =
                    ((hmac[offset] & 0x7F) << 24) |
                            ((hmac[offset + 1] & 0xFF) << 16) |
                            ((hmac[offset + 2] & 0xFF) << 8) |
                            (hmac[offset + 3] & 0xFF);
            int otp = binary % (int) Math.pow(10, config.getTokenLength());
            return String.format("%06d", otp);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate TOTP. Reason: " + ex.getMessage(), ex);
        }

    }

    private static byte[] longToBytes(long value) {
        byte[] data = new byte[8];
        for (int i = 7; value > 0; i--) {
            data[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return data;
    }
}
