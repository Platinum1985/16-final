package ru.practicum.shareit.request;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForGetList;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequest createItemRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader int requestorId) {
        return itemRequestService.addItemRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public List<ItemRequestDtoForGetList> getAllItemRequests(@RequestHeader int requestorId) { // получение всех своих запросов на вещи
        return itemRequestService.getAll(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoForGetList> getAllItemRequestsByUserId(int otherRequestorId) { // получение всех запросов на вещи другого пользователя по его id
        return itemRequestService.getAll(otherRequestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoForGetList getItemRequestByRequestId(int requestId) { // любой пользователь может получить itemRequest по id
        return itemRequestService.itemRequestDtoForGetListById(requestId);
    }
}
