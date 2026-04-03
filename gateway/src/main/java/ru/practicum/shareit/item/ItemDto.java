package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDto {
    private int id;

    @NotBlank(message = "Name cannot be blank")
    private String name; // — краткое название;

    @NotBlank(message = "Description cannot be blank")
    private String description; // — развёрнутое описание;

    @NotNull(message = "Available status is required")
    private Boolean available; // — статус о том, доступна или нет вещь для аренды;

    private int owner; // — владелец вещи;
    private int request; // — если вещь была создана по запросу другого пользователя

    public ItemDto(int id, String name, String description, Boolean available, int owner, int request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}

