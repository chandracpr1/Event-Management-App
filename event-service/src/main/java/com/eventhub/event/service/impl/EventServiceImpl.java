package com.eventhub.event.service.impl;

import com.eventhub.event.dto.CreateEventRequest;
import com.eventhub.event.dto.EventResponse;
import com.eventhub.event.entity.Event;
import com.eventhub.event.entity.SeatingType;
import com.eventhub.event.repository.EventRepository;
import com.eventhub.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public EventResponse createEvent(CreateEventRequest request, String organizerId) {

        Event event = new Event();

        event.setName(request.getName());
        event.setCategory(request.getCategory());
        //event.setImageUrl(request.getImageUrl());
        event.setImageUrl("https://4kwallpapers.com/images/walls/thumbs_3t/15328.jpeg");

        event.setVenueName(request.getVenueName());
        event.setVenueAddress(request.getVenueAddress());

        event.setCity(request.getCity());
        event.setCountry(request.getCountry());

        event.setSeatingType(SeatingType.valueOf(request.getSeatingType()));

        event.setOrganizerId(organizerId);

        event.setStatus("DRAFT");
        event.setActive(false);

        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        event.setTags(request.getTags());

        Event saved = eventRepository.save(event);

        return mapToResponse(saved);
    }

    @Override
    public List<EventResponse> getActiveEvents(String city, String category, String roles,String userId) {

        List<Event> events;
        boolean isOrganiser = roles != null && roles.contains("ORGANISER");
        if (isOrganiser && userId != null) {
            // Organisers see everything (Published + Drafts)
            if (city != null) {
                events = eventRepository.findByOrganizerIdAndCity(userId, city);
            } else {
                events = eventRepository.findByOrganizerId(userId);
            }
        } else {
            // Regular users see only Published/Active events
            if (city != null) {
                events = eventRepository.findByCityAndIsActiveTrueAndStatus(city, "PUBLISHED");
            } else {
                events = eventRepository.findByIsActiveTrueAndStatus("PUBLISHED");
            }
        }
//        if (city != null) {
//            events = eventRepository.findByCityAndIsActiveTrueAndStatus(city, "PUBLISHED");
//        } else {
//            events = eventRepository.findByIsActiveTrueAndStatus("PUBLISHED");
//        }

        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EventResponse publishEvent(String eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setStatus("PUBLISHED");
        event.setActive(true);
        event.setUpdatedAt(LocalDateTime.now());

        Event updated = eventRepository.save(event);

        return mapToResponse(updated);
    }

    private EventResponse mapToResponse(Event event) {
        EventResponse res = new EventResponse();

        res.setId(event.getId());
        res.setName(event.getName());
        res.setCategory(event.getCategory());
        res.setVenueName(event.getVenueName());
        res.setCity(event.getCity());
        res.setImageUrl(event.getImageUrl());
        res.setStatus(event.getStatus());
        res.setCreatedAt(event.getCreatedAt());
        res.setTags(event.getTags());

        return res;
    }
}
