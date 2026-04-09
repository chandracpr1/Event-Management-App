package com.eventhub.event.service;

import com.eventhub.event.entity.TimeSlot;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface TimeSlotService {

    List<String> uploadTimeSlots(String eventId, MultipartFile file);

    List<TimeSlot> getSlotsByEventAndDate(String eventId, LocalDate date);
}
