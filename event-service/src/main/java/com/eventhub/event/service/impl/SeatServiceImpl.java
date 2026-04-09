package com.eventhub.event.service.impl;

import com.eventhub.event.entity.Seat;
import com.eventhub.event.entity.TimeSlot;
import com.eventhub.event.repository.SeatRepository;
import com.eventhub.event.repository.TimeSlotRepository;
import com.eventhub.event.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Override
    public void uploadSeats(MultipartFile file) {

        List<Seat> seats = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] data = line.split(",");

                String slotId = data[0];
                String row = data[1];
                int column = Integer.parseInt(data[2]);
                String category = data[3];
                double price = Double.parseDouble(data[4]);

                // ✅ Validate slot exists
                TimeSlot slot = timeSlotRepository.findById(slotId)
                        .orElseThrow(() -> new RuntimeException("Invalid slotId"));

                // ✅ Prevent duplicate seats
                if (seatRepository.existsBySlotIdAndRowAndColumn(slotId, row, column)) {
                    throw new RuntimeException("Duplicate seat: " + row + column);
                }

                Seat seat = new Seat();
                seat.setSlotId(slotId);
                //seat.setEventId(slot.getEventId());
                seat.setRow(row);
                seat.setColumn(column);
                seat.setCategory(category);
                seat.setPrice(price);
                //seat.setActive(true);
                //seat.setVersion(0);
                //seat.setSeatNumber(row + column);

                seats.add(seat);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error processing seat file");
        }

        seatRepository.saveAll(seats);
    }
}
