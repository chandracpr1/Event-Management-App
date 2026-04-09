package com.eventhub.event.controller;


import com.eventhub.event.dto.CreateEventRequest;
import com.eventhub.event.dto.EventResponse;
import com.eventhub.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 1. Create Event
    @PostMapping
    public EventResponse createEvent(
            @RequestBody CreateEventRequest request,
            @RequestHeader("X-USER-ID") String userId
    ) {
        return eventService.createEvent(request, userId);
    }

    // 2. Get Active Events
    @GetMapping
    public List<EventResponse> getEvents(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestHeader(value = "X-USER-ID", required = false) String userId,
            @RequestHeader(value = "X-User-Roles", required = true) String roles
    ) {
        System.out.println("roles = "+roles);
        return eventService.getActiveEvents(city, category, roles,userId);
    }

    // 3. Publish Event
    @PatchMapping("/{eventId}/publish")
    public EventResponse publishEvent(@PathVariable String eventId) {
        return eventService.publishEvent(eventId);
    }
}
