package com.anthonypoon.authenticationserver.service.auth.encoder;

import com.anthonypoon.authenticationserver.service.DateTimeService;
import com.anthonypoon.authenticationserver.config.auth.AuthorizationConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTEncoder extends Encoder {
    private final DateTimeService dateTime;
    private final AuthorizationConfig config;

    public JWTEncoder(DateTimeService dateTime, AuthorizationConfig config) {
        if (StringUtils.isEmpty(config.getSecret())) {
            throw new RuntimeException("JWT Secret is not set in configuration");
        }
        this.dateTime = dateTime;
        this.config = config;
    }

    @Override
    public String encode(Map<String, Object> input, int ttl) {
        var key = Keys.hmacShaKeyFor(config.getSecret().getBytes(StandardCharsets.UTF_8));
        Date now = this.now();
        Date exp = DateUtils.addMinutes(now, ttl);
        var builder = Jwts.builder()
                .signWith(key)
                .setExpiration(exp)
                .setIssuedAt(now);
        for (Map.Entry<String, Object> pair : input.entrySet()) {
            builder.claim(pair.getKey(), pair.getValue());
        }
        return builder.compact();
    }

    @Override
    public Map<String, Object> decode(String input) {
        var key = Keys.hmacShaKeyFor(config.getSecret().getBytes(StandardCharsets.UTF_8));
        var parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
        var claims = parser.parseClaimsJws(input);
        return new HashMap<>(claims.getBody());
    }

    private Date now() {
        return Date.from(dateTime.now().toInstant());
    }
}
