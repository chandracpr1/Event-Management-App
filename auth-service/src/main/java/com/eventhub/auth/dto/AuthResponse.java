package com.eventhub.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String email;
    private String name;
    private String actExpiry;
    private String rctExpiry;
    private String accessToken;
    private String refreshToken;
}
