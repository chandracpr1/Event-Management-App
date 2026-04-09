package com.eventhub.auth.service;

import com.eventhub.auth.dto.AuthResponse;
import com.eventhub.auth.dto.SignupRequest;
import com.eventhub.auth.entity.RefreshToken;
import com.eventhub.auth.entity.Role;
import com.eventhub.auth.entity.User;
import com.eventhub.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthResponse signup(SignupRequest request) throws Exception {

        log.info("Signup request for email={}", request.getEmail());
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("User already exists: {}", request.getEmail());
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(Role.valueOf(request.getRole()));
        user.setRoles(roles);
        user.setCreatedAt(LocalDateTime.now());


        User savedUser = userRepository.save(user);
        String userId = String.valueOf(savedUser.getId());


        String accessToken = jwtService.generateToken(userId, savedUser.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());


        String actExpiry = LocalDateTime.now().plusHours(1).toString();
        String rctExpiry = refreshToken.getExpiryDate().toString();


        return new AuthResponse(
                savedUser.getEmail(),
                savedUser.getName(),
                actExpiry,
                rctExpiry,
                accessToken,
                refreshToken.getToken()
        );
    }

    public AuthResponse login(String email, String password) throws Exception {


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        log.info("user found -> {}" , user);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("Login failed for email={}", email);
            throw new RuntimeException("Invalid credentials");
        }
        log.debug("Password matched");
        String userId = String.valueOf(user.getId());


        String accessToken = jwtService.generateToken(userId, email);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());


        String actExpiry = LocalDateTime.now().plusHours(1).toString();
        String rctExpiry = refreshToken.getExpiryDate().toString();


        return AuthResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .actExpiry(actExpiry)
                .rctExpiry(rctExpiry)
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
