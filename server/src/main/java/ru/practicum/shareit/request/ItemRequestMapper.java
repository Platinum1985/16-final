package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        return new ItemRequest(
                itemRequestDto.getDescription(),
                requestor,
                LocalDateTime.now()

        );
    }
}
