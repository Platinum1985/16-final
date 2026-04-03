package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Item;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemDtoMapper {
    public static ItemDto toItemDto(Item item) {
        log.info("item in toItemDto = {}", item);
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequest() != null ? item.getRequest().getId() : 0);
    }

    public static ItemForOwnerGetDto toItemForOwnerGetDto(Item item) {
        log.info("item in toItemForOwnerGetDto = {}", item);
        return new ItemForOwnerGetDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest(),
                item.getComments() //
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }
}
