package ru.practicum.shareit.itemRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

public class ItemRequestClient extends BaseClient {

    @Autowired
    public ItemRequestClient(@Qualifier("itemRequestRestTemplate") RestTemplate restTemplate) {
        super(restTemplate); // Передаём уже настроенный RestTemplate
    }

    // Создание запроса на получение вещи
    public ResponseEntity<Object> createItemRequest(ItemRequestDto itemRequestDto, Integer userId) {
        return post("", itemRequestDto, userId);
    }

    // Получение всех запросов пользователя
    public ResponseEntity<Object> getAllItemRequests(Integer userId) {
        return get("", userId); // убрал /
    }

    public ResponseEntity<Object> getAllOtherItemRequests(Integer userId) {
        return get("/all", userId); // обращаемся к эндпоинту /all сервиса
    }

    // Получение запроса по ID
    public ResponseEntity<Object> getItemRequestByRequestId(Integer userId, Integer requestId) {
        return get(String.format("/%d", requestId), userId);
    }
}