package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exceptions.BusinessLogicException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;


@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;

    private User booker1;
    private User booker2;
    private User owner1;
    private User owner2;
    private Item item1;
    private Item item2;

    @BeforeEach
    public void setUp() {
        // Создание арендаторов
        booker1 = new User();
        booker1.setName("Booker1");
        booker1.setEmail("booker1@inbox.ru");
        userRepository.save(booker1);

        booker2 = new User();
        booker2.setName("Booker2");
        booker2.setEmail("booker2@inbox.ru");
        userRepository.save(booker2);

        // Создание владельцев предметов
        owner1 = new User();
        owner1.setName("Owner1");
        owner1.setEmail("owner1@mail.ru");
        userRepository.save(owner1);

        owner2 = new User();
        owner2.setName("Owner2");
        owner2.setEmail("owner2@mail.ru");
        userRepository.save(owner2);

        // Создание предметов аренды
        item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(owner1);
        itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(owner2);
        itemRepository.save(item2);

        // Создание тестовых бронирований с разными статусами
        bookingService.addBooking(
                new BookingDto(item1.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)),
                booker1.getId()
        ); // СТАТУС ПО УМОЛЧАНИЮ — WAITING

        bookingService.addBooking(
                new BookingDto(item2.getId(), LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(5)),
                booker2.getId()
        );

        // Меняем статус второго бронирования на APPROVED
        bookingRepository.findAll().stream()
                .findFirst()
                .map(booking -> {
                    booking.setStatus(Status.APPROVED);
                    return bookingRepository.save(booking);
                })
                .orElse(null);

        // Создаём третье бронирование и меняем статус на REJECTED
        bookingService.addBooking(
                new BookingDto(item1.getId(), LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3)),
                booker2.getId()
        );

        bookingRepository.findAll().stream()
                .skip(1) // пропускаем первое бронирование
                .findFirst()
                .map(booking -> {
                    booking.setStatus(Status.REJECTED);
                    return bookingRepository.save(booking);
                })
                .orElse(null);
    }

    @Test
    public void testAddBooking() {
        BookingDto bookingDto = new BookingDto(
                item1.getId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3)
        );

        Booking booking = bookingService.addBooking(bookingDto, booker1.getId());

        assertThat(booking.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(booking.getItem()).isEqualTo(item1);
        assertThat(booking.getBooker()).isEqualTo(booker1);
    }

    @Test
    public void testUpdateBookingStatus_Approved() throws BusinessLogicException {
        // Получаем ID первого бронирования (статус WAITING)
        int bookingId = bookingRepository.findAll().get(0).getId();

        // Обновляем статус на APPROVED
        Booking updatedBooking = bookingService.updateBookingStatus(bookingId, true, owner1.getId());

        assertThat(updatedBooking.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(item1.getAvailable()).isFalse(); // Предмет должен стать недоступным
    }

    @Test
    public void testUpdateBookingStatus_Rejected() throws BusinessLogicException {
        int bookingId = bookingRepository.findAll().get(1).getId(); // Второе бронирование

        Booking updatedBooking = bookingService.updateBookingStatus(bookingId, false, owner2.getId());

        assertThat(updatedBooking.getStatus()).isEqualTo(Status.REJECTED);
        assertThat(item2.getAvailable()).isTrue(); // Доступность предмета не меняется
    }

    @Test
    public void testGetBookingByBookerId() {
        int bookingId = bookingRepository.findAll().get(0).getId();
        Booking booking = bookingService.getBookingByBookerIdOrOwnerId(bookingId, booker1.getId());
        assertThat(booking.getBooker().getId()).isEqualTo(booker1.getId());
    }

    @Test
    public void testGetBookingByOwnerId() {
        int bookingId = bookingRepository.findAll().get(0).getId();
        Booking booking = bookingService.getBookingByBookerIdOrOwnerId(bookingId, owner1.getId());
        assertThat(booking.getItem().getOwner().getId()).isEqualTo(owner1.getId());
    }

    @Test
    public void testGetBookerBookings_All() {
        List<Booking> bookings = bookingService.getBookerBookings(booker1.getId(), BookingState.ALL);
        assertFalse(bookings.isEmpty(), "Список бронирований должен быть не пустым");
    }

    @Test
    public void testGetOwnerBookings_Future() {
        List<Booking> futureBookings = bookingService.getOwnerBookings(owner1.getId(), "FUTURE");
        for (Booking booking : futureBookings) {
            assertThat(booking.getStart()).isAfter(LocalDateTime.now()); // Все бронирования в будущем
        }
    }

    @Test
    public void testAddBookingValidationError() {
        BookingDto invalidBookingDto = new BookingDto(item1.getId(), LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(2)); // Некорректные даты

        try {
            bookingService.addBooking(invalidBookingDto, booker1.getId());
            fail("Ожидалось исключение ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getMessage()).contains("Некорректно заполнены поля booking");
        }
    }

    @Test
    public void testCheckItemExists() {
        boolean exists = bookingService.checkItemExists(item1.getId());
        assertThat(exists).isTrue();

        boolean notExists = bookingService.checkItemExists(-1); // ID несуществующего item'а
        assertThat(notExists).isFalse();
    }

    @Test
    public void testAddBookingForUnavailableItem() {
        Item unavailableItem = new Item();
        unavailableItem.setName("Unavailable Item");
        unavailableItem.setDescription("Unavailable Description");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwner(owner1);
        itemRepository.save(unavailableItem);

        BookingDto bookingDto = new BookingDto(unavailableItem.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        try {
            bookingService.addBooking(bookingDto, booker1.getId());
            fail("Ожидалось исключение ValidationException");
        } catch (ValidationException e) {
            assertThat(e.getMessage()).contains("Item недоступен для аренды");
        }
    }
}

