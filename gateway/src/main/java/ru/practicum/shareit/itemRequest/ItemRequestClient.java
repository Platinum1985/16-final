package ru.practicum.shareit.itemRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;


@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
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