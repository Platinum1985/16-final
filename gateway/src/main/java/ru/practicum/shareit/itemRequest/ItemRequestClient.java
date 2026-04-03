package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class ItemRequestClient extends BaseClient {
    private final ObjectMapper objectMapper;

    @Autowired
    public ItemRequestClient(RestTemplate rest, ObjectMapper objectMapper) {
        super(rest);
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<Object> createItemRequest(ItemRequestDto itemRequestDto, Integer userId) {
        return post("/requests", userId, itemRequestDto);
    }

    public ResponseEntity<List<ItemRequestDtoForGetList>> getAllItemRequests(Integer userId) {
        ResponseEntity<Object> response = get("/requests", userId);

        if (!response.hasBody()) {
            return ResponseEntity.status(response.getStatusCode()).build();
        }

        String bodyString = convertBodyToString(response.getBody());

        try {
            List<ItemRequestDtoForGetList> requests = objectMapper.readValue(bodyString, new com.fasterxml.jackson.core.type.TypeReference<>() {
            });
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(requests);
        } catch (JsonProcessingException e) {
            // Логирование ошибки
            System.err.println("Ошибка десериализации при получении списка запросов: " + e.getMessage());
            return ResponseEntity
                    .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of()); // Возвращаем пустой список
        }
    }

    public ResponseEntity<ItemRequestDtoForGetList> getItemRequestByRequestId(Integer userId, int requestId) {
        String path = String.format("/requests/%d", requestId);
        ResponseEntity<Object> response = get(path, userId);

        if (!response.hasBody()) {
            return ResponseEntity.status(response.getStatusCode()).build();
        }

        String bodyString = convertBodyToString(response.getBody());

        try {
            ItemRequestDtoForGetList request = objectMapper.readValue(bodyString, ItemRequestDtoForGetList.class);
            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(request);
        } catch (JsonProcessingException e) {
            // Логирование ошибки
            System.err.println("Ошибка десериализации запроса по ID: " + e.getMessage());
            return ResponseEntity
                    .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    private String convertBodyToString(Object body) {
        if (body == null) {
            return "";
        }
        if (body instanceof byte[]) {
            return new String((byte[]) body, StandardCharsets.UTF_8);
        } else {
            return body.toString();
        }
    }
}
