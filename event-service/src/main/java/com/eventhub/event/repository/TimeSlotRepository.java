package com.eventhub.event.repository;


import com.eventhub.event.entity.TimeSlot;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeSlotRepository extends MongoRepository<TimeSlot, String> {

    List<TimeSlot> findByEventIdAndStartTimeBetween(
            String eventId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<TimeSlot> findByEventId(String eventId);
}