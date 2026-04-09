package com.eventhub.event.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateEventRequest {

    private String name;
    private String category;
    private String imageUrl;

    private String venueName;
    private String venueAddress;

    private String city;
    private String country;

    private String seatingType;

    private List<String> tags;
}
