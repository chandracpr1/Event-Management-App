package com.eventhub.event.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Document(collection = "events")
@Data
@CompoundIndex(name = "event_search_idx", def = "{'city':1, 'status':1, 'isActive':1}")
public class Event {

    @Id
    private String id;
    @NotBlank
    private String name;

    private String organizerId;

    private String category;

    private String imageUrl;

    private String venueName;
    private String venueAddress;

    private String city;
    private String country;

    private SeatingType seatingType;

    private String status; // DRAFT, PUBLISHED, CANCELLED

    private boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<String> tags;
}
