package com.eventhub.event.controller;

import com.eventhub.event.entity.TimeSlot;
import com.eventhub.event.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/timeslots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    // Upload CSV
    @PostMapping("/upload/{eventId}")
    public List<String> uploadSlots(
            @PathVariable String eventId,
            @RequestParam("file") MultipartFile file
    ) {
        return timeSlotService.uploadTimeSlots(eventId, file);
    }

    // Get slots by date
    @GetMapping("/{eventId}")
    public List<TimeSlot> getSlots(
            @PathVariable String eventId,
            @RequestParam String date
    ) {
        return timeSlotService.getSlotsByEventAndDate(
                eventId,
                LocalDate.parse(date)
        );
    }
}
