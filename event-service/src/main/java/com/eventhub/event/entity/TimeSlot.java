package com.eventhub.event.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "timeslots")
@Data
public class TimeSlot {

    @Id
    private String id;

    private String eventId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int totalSeats;
    private int availableSeats;

    private boolean isActive;

    private String status; // OPEN, CLOSED, SOLD_OUT

    private int version; // for optimistic locking

    private LocalDateTime createdAt;
}