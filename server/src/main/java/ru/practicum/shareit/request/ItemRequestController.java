package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForGetList;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequest createItemRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader("X-Sharer-User-Id") int requestorId) {
        return itemRequestService.addItemRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public List<ItemRequestDtoForGetList> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Integer requestorId) { // получение всех своих запросов на вещи
        log.info("RequestorId = {}", requestorId);
        return itemRequestService.getAllUserRequests(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoForGetList> getAllOtherRequests(@RequestHeader("X-Sharer-User-Id") Integer requestorId) { // получение всех запросов на вещи других пользователей
        return itemRequestService.getAllOtherRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoForGetList getItemRequestByRequestId(@PathVariable Integer requestId) { // любой пользователь может получить itemRequest по id
        return itemRequestService.itemRequestDtoForGetListById(requestId);
    }
}