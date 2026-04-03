package ru.practicum.shareit.request.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ItemRequestDto {
    private String description;
}
