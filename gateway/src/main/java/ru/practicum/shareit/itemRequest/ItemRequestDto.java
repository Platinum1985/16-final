package ru.practicum.shareit.itemRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ItemRequestDto {
    @NotBlank(message = "description не должно быть пустым")
    private String description;
}

