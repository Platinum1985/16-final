package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    // Тестовые данные
    private User testUser;
    private Item testItem;
    private BookingDto testBookingDto;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testUser = new User(1, "Test User", "test@example.com");
        testItem = new Item(1, "Laptop", "A powerful laptop for work", true, testUser);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);

        // Создаём тестовый объект с датами
        testBookingDto = new BookingDto(testItem.getId(), start, end);
        testBooking = BookingDtoMapper.toBooking(testBookingDto, testItem, testUser, Status.WAITING);
    }

    @Test
    void addBooking_ShouldCreateNewBooking() throws Exception {
        // Берём даты из тестового объекта — так мы точно проверяем то, что отправили
        LocalDateTime start = testBookingDto.getStart();
        LocalDateTime end = testBookingDto.getEnd();

        // Форматируем даты в строку, включая миллисекунды — это нужно для сравнения с ответом API
        String startFormatted = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"));
        String endFormatted = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"));

        when(bookingService.addBooking(any(BookingDto.class), eq(1)))
                .thenReturn(testBooking);

        String jsonRequest = objectMapper.writeValueAsString(testBookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.start").value(startFormatted))
                .andExpect(jsonPath("$.end").value(endFormatted));
    }

    @Test
    void updateBookingApproval_ShouldUpdateApprovalStatus() throws Exception {
        when(bookingService.updateBookingStatus(anyInt(), anyBoolean(), anyInt()))
                .thenReturn(testBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", "1"))

                .andExpect(status().isOk());
    }

    @Test
    void getBookingByBooker_ShouldReturnBooking() throws Exception {
        when(bookingService.getBookingByBookerIdOrOwnerId(eq(1), eq(1)))
                .thenReturn(testBooking);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()));
    }

    @Test
    void getBookerBookings_ShouldReturnBookerBookings() throws Exception {
        // Создаем список бронирований с использованием нового конструктора и добавляем ID отдельно
        Booking booking1 = new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(7), testItem, testUser, Status.WAITING);
        booking1.setId(1); // Добавляем ID отдельно

        Booking booking2 = new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(7), testItem, testUser, Status.WAITING);
        booking2.setId(2); // Добавляем ID отдельно

        List<Booking> bookings = List.of(booking1, booking2);

        when(bookingService.getBookerBookings(eq(1), isNull()))
                .thenReturn(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", "1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getUserBookings_ShouldReturnOwnerBookings() throws Exception {
        Booking testBooking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(7), testItem, testUser, Status.WAITING);
        testBooking.setId(1); // Добавляем ID отдельно

        List<Booking> ownerBookings = List.of(testBooking);

        when(bookingService.getOwnerBookings(anyInt(), isNull()))
                .thenReturn(ownerBookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getUserBookingsWithState_ShouldFilterByState() throws Exception {
        Booking testBooking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(7), testItem, testUser, Status.WAITING);
        testBooking.setId(1); // Добавляем ID отдельно

        List<Booking> ownerBookings = List.of(testBooking);
        String state = "current";

        when(bookingService.getOwnerBookings(eq(1), eq(state)))
                .thenReturn(ownerBookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", state))

                .andExpect(status().isOk());
    }
}
