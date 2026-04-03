package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemForOwnerGetDto {
    private int id;
    private String name; //— краткое название;
    private String description; //— развёрнутое описание;
    private Boolean available; // — статус о том, доступна или нет вещь для аренды;
    private User owner; //— владелец вещи;
    private ItemRequest request; //— если вещь была создана по запросу другого пол
    private List<Comment> comments = new ArrayList<>();
    private Booking lastBooking;
    private Booking nextBooking;

    public ItemForOwnerGetDto(int id, String name, String description, Boolean available, User owner, ItemRequest request, List<Comment> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
        this.comments = comments;
    }
}

