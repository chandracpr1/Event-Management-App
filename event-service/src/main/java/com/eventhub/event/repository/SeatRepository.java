package com.eventhub.event.repository;


import com.eventhub.event.entity.Seat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SeatRepository extends MongoRepository<Seat, String> {

    boolean existsBySlotIdAndRowAndColumn(String slotId, String row, int column);

    List<Seat> findBySlotId(String slotId);
}
