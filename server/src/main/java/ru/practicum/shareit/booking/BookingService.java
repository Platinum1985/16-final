package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataBaseException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public Booking addBooking(BookingDto bookingDto, int bookerId) {
        log.info("bookingDto после контроллера = {}", bookingDto);
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new DataBaseException("нет такого пользователя booker"));
        if (!checkItemExists(bookingDto.getItemId())) {
            throw new NotFoundException("Item не найден");
        }

        if (!itemRepository.findById(bookingDto.getItemId()).get().getAvailable()) {
            log.error("Item недоступен для аренды");
            throw new ValidationException("Item недоступен для аренды");
        }
        if (!validationBooking(bookingDto)) {
            throw new ValidationException("Некорректно заполнены поля booking");
        }
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found"));
        Booking booking = BookingDtoMapper.toBooking(bookingDto, item, booker, Status.WAITING);
        log.info("booking перед добавлением в репозиторий = {}", booking);
        return bookingRepository.save(booking);

    }

    public Booking updateBookingStatus(int bookingId, boolean approved, int itemOwnerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new DataBaseException("Такого booking нет"));
        if (booking.getItem().getOwner().getId() != itemOwnerId) {
            throw new DataBaseException("У item другой владелец");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
            booking.getItem().setAvailable(false);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    public Booking getBookingByBookerIdOrOwnerId(int bookingId, int bookerIdOrOwnerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking не найден"));
        if (booking.getBooker().getId() != bookerIdOrOwnerId && booking.getItem().getOwner().getId() != bookerIdOrOwnerId) {
            throw new DataBaseException("У пользователя нет такого Booking");
        }
        return booking;
    }

    public List<Booking> getBookerBookings(int userId, BookingState state) {
        log.info("Retrieving bookings for user {} with state: {}", userId, state);

        if (state == null || state.equals(BookingState.ALL)) {
            return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        } else if (state.equals(BookingState.CURRENT)) {
            LocalDateTime now = LocalDateTime.now();
            return bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
        } else if (state.equals(BookingState.PAST)) {
            LocalDateTime now = LocalDateTime.now();
            return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
        } else if (state.equals(BookingState.FUTURE)) {
            LocalDateTime now = LocalDateTime.now();
            return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
        } else if (state.equals(BookingState.WAITING)) {
            return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
        } else if (state.equals(BookingState.REJECTED)) {
            return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        } else {
            throw new ValidationException("Invalid state parameter");
        }
    }

    public List<Booking> getOwnerBookings(int userId, String state) {
        log.info("Retrieving bookings for owner {} with state: {}", userId, state);
        if (bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId).isEmpty()) {
            throw new DataBaseException("Нет Booking у пользователя");
        }
        if (state == null || state.equals("ALL")) {
            return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        } else if (state.equals("CURRENT")) {
            LocalDateTime now = LocalDateTime.now();
            return bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
        } else if (state.equals("PAST")) {
            LocalDateTime now = LocalDateTime.now();
            return bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
        } else if (state.equals("FUTURE")) {
            LocalDateTime now = LocalDateTime.now();
            return bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
        } else if (state.equals("WAITING")) {
            return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
        } else if (state.equals("REJECTED")) {
            return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        } else {
            throw new ValidationException("Invalid state parameter");
        }
    }

    public boolean validationBooking(BookingDto bookingDto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        if (start == null || end == null) {
            log.error("Поля start или end не заполнены");
            return false;
        }

        if (start.isBefore(now) || end.isBefore(now)) {
            log.error("Даты бронирования не могут быть в прошлом");
            return false;
        }

        if (!start.isBefore(end)) {
            log.error("Дата начала бронирования должна быть раньше даты окончания");
            return false;
        }

        return true;
    }

    public boolean checkItemExists(int itemId) {
        return itemRepository.findById(itemId).isPresent();
    }
}