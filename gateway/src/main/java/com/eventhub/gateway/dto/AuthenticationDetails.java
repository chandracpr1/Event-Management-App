package com.eventhub.gateway.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationDetails {
    private String userId;
    private String name;
    private String roles;
    private String email;
}
