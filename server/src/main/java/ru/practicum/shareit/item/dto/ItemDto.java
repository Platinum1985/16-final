package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemDto {
    private int id;
    private String name; //— краткое название;
    private String description; //— развёрнутое описание;
    private Boolean available; // — статус о том, доступна или нет вещь для аренды;
    private int owner; //— владелец вещи;
    private int requestId; //— если вещь была создана по запросу другого пол

    public ItemDto(int id, String name, String description, Boolean available, int owner, int requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.requestId = requestId;

    }
}
