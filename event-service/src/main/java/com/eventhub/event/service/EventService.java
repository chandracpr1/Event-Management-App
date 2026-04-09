package com.eventhub.event.service;

import com.eventhub.event.dto.CreateEventRequest;
import com.eventhub.event.dto.EventResponse;

import java.util.List;

public interface EventService {
    EventResponse createEvent(CreateEventRequest request, String organizerId);

    List<EventResponse> getActiveEvents(String city, String category, String roles,String userId);

    EventResponse publishEvent(String eventId);
}
