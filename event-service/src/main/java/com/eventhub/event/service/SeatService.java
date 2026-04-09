package com.eventhub.event.service;

import org.springframework.web.multipart.MultipartFile;

public interface SeatService {

    void uploadSeats(MultipartFile file);
}