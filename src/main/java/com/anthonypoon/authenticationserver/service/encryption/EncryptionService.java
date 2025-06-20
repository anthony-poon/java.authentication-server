package com.anthonypoon.authenticationserver.service.encryption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Service
public class EncryptionService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String KEY_ALGO = "AES";
    private static final String CIPHER_ALGO = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16;
    private final EncryptionConfig config;

    public EncryptionService(EncryptionConfig config) {
        this.config = config;
    }

    public String encrypt(String input) {
        return this.encrypt(input.getBytes());
    }

    public String encrypt(byte[] input) {
        try {
            var secret = Base64.getDecoder().decode(this.config.getKey());
            var key = new SecretKeySpec(secret, KEY_ALGO);
            var iv = this.random(IV_LENGTH);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            var cipherText = cipher.doFinal(input);
            var combined = new byte[IV_LENGTH + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(cipherText, 0, combined, IV_LENGTH, cipherText.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to encrypt input. Reason: " + ex.getMessage(), ex);
        }
    }

    public byte[] decrypt(String input) {
        try {
            var secret = Base64.getDecoder().decode(this.config.getKey());
            var combined = Base64.getDecoder().decode(input);
            var iv = new byte[IV_LENGTH];
            var cipherText = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, cipherText, 0, cipherText.length);
            var key = new SecretKeySpec(secret, KEY_ALGO);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            return cipher.doFinal(cipherText);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to decrypt input. Reason: " + ex.getMessage(), ex);
        }
    }

    public byte[] hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private byte[] random(int length) {
        var rtn = new byte[length];
        SECURE_RANDOM.nextBytes(rtn);
        return rtn;
    }
}
