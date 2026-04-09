package com.eventhub.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    @Column(unique = true)
    private String token;

    private LocalDateTime expiryDate;
}
