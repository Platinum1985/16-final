package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") int bookerId) {
        log.info("booking = {} in controller", bookingDto);
        return bookingService.addBooking(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public Booking updateBookingApproval(@PathVariable int bookingId, @RequestParam(value = "approved", defaultValue = "false") boolean approved, @RequestHeader("X-Sharer-User-Id") int itemOwnerId) {
        log.info("Updating booking {} with approval status: {}", bookingId, approved);
        return bookingService.updateBookingStatus(bookingId, approved, itemOwnerId);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingByBooker(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int bookerIdOrOwnerId) {
        return bookingService.getBookingByBookerIdOrOwnerId(bookingId, bookerIdOrOwnerId);
    }

    @GetMapping
    public List<Booking> getBookerBookings(@RequestHeader("X-Sharer-User-Id") int bookerId, @RequestParam(value = "state", required = false) BookingState state) {
        log.info("Getting bookings for booker {} with state: {}", bookerId, state);
        return bookingService.getBookerBookings(bookerId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getUserBookings(@RequestHeader("X-Sharer-User-Id") int ownerId, @RequestParam(value = "state", required = false) String state) {
        log.info("Getting bookings for owner {} with state: {}", ownerId, state);
        return bookingService.getOwnerBookings(ownerId, state);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoFoundIdException(NotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneralException(Exception e) {
        return Map.of("error", e.getMessage());
    }
}

