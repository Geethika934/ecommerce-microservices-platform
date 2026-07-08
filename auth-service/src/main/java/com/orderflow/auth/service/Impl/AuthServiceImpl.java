package com.orderflow.auth.service.Impl;

import com.orderflow.auth.dto.request.LoginRequest;
import com.orderflow.auth.dto.request.RegisterRequest;
import com.orderflow.auth.dto.response.AuthResponse;
import com.orderflow.auth.entity.User;
import com.orderflow.auth.enums.Role;
import com.orderflow.auth.exception.TokenException;
import com.orderflow.auth.repository.UserRepository;
import com.orderflow.auth.service.AuthService;
import com.orderflow.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.CUSTOMER)
                .enabled(true) // default role
                .build();
        userRepository.save(user);
        return generateTokens(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getClass().getName() + " - " + e.getMessage());
            throw e;
        }
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        return generateTokens(user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        // Must be a refresh token type
        if (!"refresh".equals(jwtService.extractTokenType(refreshToken))) {
            throw new TokenException("Invalid token type");
        }
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(username).orElseThrow();

        // Validate refresh token from Redis
        String storedToken = redisTemplate.opsForValue().get("refresh:" + username);
        if (!refreshToken.equals(storedToken)) {
            throw new TokenException("Refresh token mismatch or expired");
        }

        return generateTokens(user);   // issue new pair (rotation)
    }

    @Override
    public void logout(String accessToken, String username) {
        // Block this access token for the remainder of its life
        Date expiry = jwtService.extractExpiration(accessToken);
        long ttl = expiry.getTime() - System.currentTimeMillis();
        redisTemplate.opsForValue().set(
                "blocklist:" + accessToken,
                "revoked",
                ttl, TimeUnit.MILLISECONDS
        );
        // Delete refresh token too
        redisTemplate.delete("refresh:" + username);
    }

    private AuthResponse generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Store refresh token in Redis (replaces old one = rotation)
        redisTemplate.opsForValue().set(
                "refresh:" + user.getEmail(),
                refreshToken,
                refreshTokenExpiry, TimeUnit.MILLISECONDS
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().name())
                .build();
    }
}
