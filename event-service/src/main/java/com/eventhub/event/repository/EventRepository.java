
package com.eventhub.event.repository;

import com.eventhub.event.entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findByIsActiveTrueAndStatus(String status);

    List<Event> findByCityAndIsActiveTrueAndStatus(String city, String status);

    List<Event> findByOrganizerId(String organizerId);
    List<Event> findByOrganizerIdAndCity(String organizerId, String city);
}