package com.eventhub.auth.controller;

import com.eventhub.auth.dto.AuthResponse;
import com.eventhub.auth.dto.LoginRequest;
import com.eventhub.auth.dto.RefreshTokenRequest;
import com.eventhub.auth.dto.SignupRequest;
import com.eventhub.auth.entity.RefreshToken;
import com.eventhub.auth.entity.User;
import com.eventhub.auth.repository.UserRepository;
import com.eventhub.auth.service.AuthService;
import com.eventhub.auth.service.JwtService;
import com.eventhub.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) throws Exception {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) throws Exception {
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);


    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) throws Exception {

        // 1. Verify the refresh token using the string from the DTO
        RefreshToken validToken = refreshTokenService.verifyToken(request.getRefreshToken());


        User user = userRepository.findById(validToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found for the given refresh token"));


        String newAccessToken = jwtService.generateToken(String.valueOf(user.getId()), user.getEmail());


        String actExpiry = LocalDateTime.now().plusHours(1).toString();
        String rctExpiry = validToken.getExpiryDate().toString();


        AuthResponse response = AuthResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .actExpiry(actExpiry)
                .rctExpiry(rctExpiry)
                .accessToken(newAccessToken)
                .refreshToken(validToken.getToken()) // We return the same valid refresh token
                .build();

        return ResponseEntity.ok(response);
    }

}
