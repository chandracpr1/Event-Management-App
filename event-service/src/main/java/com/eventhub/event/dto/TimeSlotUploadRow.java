package com.eventhub.event.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeSlotUploadRow {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int totalSeats;
}
