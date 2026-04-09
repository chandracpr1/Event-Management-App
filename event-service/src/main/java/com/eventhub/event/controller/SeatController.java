package com.eventhub.event.controller;

import com.eventhub.event.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping("/upload")
    public String uploadSeats(@RequestParam("file") MultipartFile file) {
        seatService.uploadSeats(file);
        return "Seats uploaded successfully";
    }
}