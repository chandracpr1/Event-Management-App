package com.eventhub.event.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventResponse {

    private String id;
    private String name;
    private String category;

    private String venueName;
    private String city;

    private String imageUrl;

    private String status;

    private LocalDateTime createdAt;

    private List<String> tags;
}
