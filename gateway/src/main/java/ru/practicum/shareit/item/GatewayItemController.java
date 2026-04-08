package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;


import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/items")
public class GatewayItemController {
    private final ItemClient baseClient;

    @Autowired
    public GatewayItemController(ItemClient baseClient) {
        this.baseClient = baseClient;
    }

    // Создание нового элемента
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return baseClient.postItem("", itemDto, ownerId);
    }

    // Получение элемента по ID
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemByOwnerId(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return baseClient.getItem("/" + itemId, ownerId);
    }

    // Добавление комментария к элементу
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable int itemId, @RequestBody CommentRequest commentRequest,
                                             @RequestHeader("X-Sharer-User-Id") int authorId) {
        return baseClient.createComment("/" + itemId + "/comment", commentRequest, authorId);
    }

    // Обновление элемента
    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateItem(@PathVariable int itemId, @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return baseClient.patchItem("/" + itemId, itemDto, ownerId);
    }

    // Получение всех элементов для владельца
    @GetMapping
    public ResponseEntity<Object> getAllItemsForOwner(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return baseClient.getItem("/", ownerId);
    }

    // Поиск элементов
    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String text) {
        return baseClient.searchItems(text);
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

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUnexpectedException(Exception e) {
        log.error("Unexpected error occurred", e);
        return Map.of("error", "Internal server error");
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleGeneralException(DuplicateEmailException e) {
        return Map.of("Duplicate email", e.getMessage());
    }
}