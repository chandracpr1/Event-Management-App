package com.eventhub.event.service.impl;

import com.eventhub.event.entity.Event;
import com.eventhub.event.entity.TimeSlot;
import com.eventhub.event.repository.EventRepository;
import com.eventhub.event.repository.TimeSlotRepository;
import com.eventhub.event.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final EventRepository eventRepository;

    @Override
    public List<String> uploadTimeSlots(String eventId, MultipartFile file) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<TimeSlot> slots = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] data = line.split(",");

                LocalDateTime start = LocalDateTime.parse(data[0]);
                LocalDateTime end = LocalDateTime.parse(data[1]);
                int totalSeats = Integer.parseInt(data[2]);

                validateSlot(eventId, start, end);

                TimeSlot slot = new TimeSlot();
                slot.setEventId(eventId);
                slot.setStartTime(start);
                slot.setEndTime(end);
                slot.setTotalSeats(totalSeats);
                slot.setAvailableSeats(totalSeats);
                slot.setStatus("OPEN");
                slot.setActive(true);
                slot.setVersion(0);
                slot.setCreatedAt(LocalDateTime.now());

                slots.add(slot);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error processing file");
        }

        List<TimeSlot> saved = timeSlotRepository.saveAll(slots);

        return saved.stream()
                .map(TimeSlot::getId)
                .toList();
    }

    // 🔥 OVERLAP VALIDATION
    private void validateSlot(String eventId, LocalDateTime start, LocalDateTime end) {

        if (start.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot create past slot");
        }

//        List<TimeSlot> existing = timeSlotRepository.findByEventId(eventId);
//
//        for (TimeSlot slot : existing) {
//            boolean overlap =
//                    start.isBefore(slot.getEndTime()) &&
//                            end.isAfter(slot.getStartTime());
//
//            if (overlap) {
//                throw new RuntimeException("Slot overlaps with existing slot");
//            }
//        }
    }

    @Override
    public List<TimeSlot> getSlotsByEventAndDate(String eventId, LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return timeSlotRepository
                .findByEventIdAndStartTimeBetween(eventId, start, end)
                .stream()
                .filter(slot -> slot.getStartTime().isAfter(LocalDateTime.now()))
                .toList();
    }
}
