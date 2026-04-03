package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDtoForGetList {
    private int id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemDtoForItemRequestList> items;

    public ItemRequestDtoForGetList(int id, String description, User requestor, LocalDateTime created, List<ItemDtoForItemRequestList> items) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
        this.created = created;
        this.items = items;
    }
}