package ru.practicum.shareit.itemRequest;

import lombok.Data;

@Data
public class ItemDtoForItemRequestList {
    private int id;
    private String name;
    private int ownerId;

    public ItemDtoForItemRequestList(int id, String name, int ownerId) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
    }
}