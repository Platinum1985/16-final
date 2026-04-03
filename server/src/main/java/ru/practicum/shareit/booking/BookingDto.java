package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private int itemId;
    private LocalDateTime start;
    private LocalDateTime end;

    public BookingDto(int itemId, LocalDateTime start, LocalDateTime end) {
        this.itemId = itemId;
        this.start = start;
        this.end = end;
    }
}
