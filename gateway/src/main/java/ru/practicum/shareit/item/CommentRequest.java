package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CommentRequest {
    @NotBlank(message = "Комментарий не должен быть пустым")
    private String text;
}
