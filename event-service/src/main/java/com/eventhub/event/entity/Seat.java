package com.eventhub.event.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "seats")
@Data
public class Seat {

    @Id
    private String id;

    private String slotId;

    private String row;
    private int column;

    private String category;

    private double price;

    // Optional future
    private String section;
}
