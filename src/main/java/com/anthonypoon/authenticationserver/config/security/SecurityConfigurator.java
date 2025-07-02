package com.anthonypoon.authenticationserver.config.security;

import com.anthonypoon.authenticationserver.config.security.service.ServiceAuthenticationFilter;
import com.anthonypoon.authenticationserver.config.security.service.ServiceAuthenticationManager;
import com.anthonypoon.authenticationserver.config.security.web.WebAuthenticationFilter;
import com.anthonypoon.authenticationserver.config.security.web.WebAuthenticationManager;
import com.anthonypoon.authenticationserver.domains.role.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfigurator {
    private final SecurityConfiguration configuration;

    public SecurityConfigurator(SecurityConfiguration configuration) {
        this.configuration = configuration;
    }

    private CorsConfigurationSource getCorsSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(configuration.getAllowedOrigins()));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("Content-Disposition");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain webFilterChain(HttpSecurity http, WebAuthenticationFilter filter, WebAuthenticationManager manager) throws Exception {
        http
                .authenticationManager(manager)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(getCorsSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**").hasRole(UserRole.USER.toString())
                        .requestMatchers("/admin/**").hasRole(UserRole.ADMIN.toString())
                        .requestMatchers(
                                "/heartbeat",
                                "/authorize/**",
                                "/error"
                        ).permitAll()
                        .anyRequest().denyAll()
                )
                .addFilter(filter);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain serviceFilterChain(HttpSecurity http, ServiceAuthenticationFilter filter, ServiceAuthenticationManager manager) throws Exception {
        http
                .securityMatcher("/service/**")
                .authenticationManager(manager)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilter(filter);
        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
