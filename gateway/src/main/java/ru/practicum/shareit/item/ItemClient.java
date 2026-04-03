package ru.practicum.shareit.item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

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
    public ResponseEntity<Object> createComment(String path, CommentRequest commentRequest) {
        return post(path, commentRequest);
    }

    // Обёртка для POST (создание элементов)
    public ResponseEntity<Object> postItem(String path, ItemDto itemDto) {
        return post(path, itemDto);
    }

    // Обёртка для GET
    public ResponseEntity<Object> getItem(String path) {
        return get(path);
    }

    // Обёртка для PATCH
    public ResponseEntity<Object> patchItem(String path, ItemDto itemDto) {
        return patch(path, itemDto);
    }
}