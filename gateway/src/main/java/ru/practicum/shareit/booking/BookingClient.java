package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;


public class BookingClient extends BaseClient {

    @Autowired
    public BookingClient(@Qualifier("bookingRestTemplate") RestTemplate restTemplate) {
        super(restTemplate); // Передаём уже настроенный RestTemplate
    }

    public ResponseEntity<Object> getBookings(int userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }


    public ResponseEntity<Object> bookItem(int userId, BookItemRequestDto requestDto) {
        return post("", requestDto, userId);
    }

    public ResponseEntity<Object> getBooking(int userId, Integer bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> updateBookingStatus(int bookingId, String approved, int itemOwnerId) {

        // Формируем query‑параметры
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );

        // Строка пути — только ID бронирования
        String path = "/" + bookingId + "?approved={approved}";

        // Отправляем PATCH‑запрос без тела, только с параметрами
        return patch(path, parameters, itemOwnerId);
    }
}
