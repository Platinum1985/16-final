package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    // Обёртка для POST (создание комментариев)
    public ResponseEntity<Object> createComment(String path, CommentRequest commentRequest, Integer userId) {
        return post(path, commentRequest, userId);
    }

    // Обёртка для POST (создание элементов)
    public ResponseEntity<Object> postItem(String path, ItemDto itemDto, Integer userId) {
        return post(path, itemDto, userId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search", null, parameters);
    }

    // Обёртка для GET
    public ResponseEntity<Object> getItem(String path, Integer userId) {
        return get(path, userId);
    }

    // Обёртка для PATCH
    public ResponseEntity<Object> patchItem(String path, ItemDto itemDto, Integer userId) {
        return patch(path, itemDto, userId);
    }
}