package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerGetDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        itemDto.setOwner(ownerId);
        log.info("itemDto = {} in controller", itemDto);
        return itemService.addItem(itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemForOwnerGetDto getItemByOwnerId(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("itemId, userId in controller ItemForOwnerGetDto getItemByOwnerId ==={}  === {}", itemId, ownerId);
        return itemService.getItemByOwner(itemId, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public Comment addComment(@PathVariable int itemId, @RequestBody CommentRequest commentRequest,
                              @RequestHeader("X-Sharer-User-Id") int authorId) {
        return itemService.addComment(itemId, commentRequest, authorId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Item updateItem(@PathVariable int itemId,
                           @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") int ownerId) {
        itemDto.setId(itemId);
        itemDto.setOwner(ownerId);
        return itemService.patchItem(itemDto, ownerId);
    }


    @GetMapping
    public List<Item> getAllItemsForOwner(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemService.getAllItemsForOwner(ownerId);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam("text") String text) {
        log.info("in controller text = {}", text);
        return itemService.searchAvailableItems(text);
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

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneralException(Exception e) {
        return Map.of("error внутр ошибка бд", e.getMessage());
    }
}
