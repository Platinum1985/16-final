package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingDtoMapper {
    public static Booking toBooking(BookingDto bookingDto, Item item, User booker, Status status) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                status
        );
    }
}
