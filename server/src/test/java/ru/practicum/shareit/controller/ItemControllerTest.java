package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRequest;
import ru.practicum.shareit.exceptions.NotFoundException;

import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemForOwnerGetDto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    // Тестовые данные
    private Item testItem;
    private User testUser;
    private ItemDto testItemDto;
    private ItemForOwnerGetDto testItemForOwnerDto;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых объектов
        testUser = new User(1, "Test User", "test@example.com");
        testItem = new Item(1, "Laptop", "A powerful laptop for work", true, testUser);
        testItemDto = new ItemDto();
        testItemDto.setName("Laptop");
        testItemDto.setDescription("A powerful laptop for work");
        testItemDto.setAvailable(true);
        testItemDto.setOwner(1);

        testItemForOwnerDto = ItemDtoMapper.toItemForOwnerGetDto(testItem);
    }

    @Test
    void getItemByOwner_ShouldReturnItemWhenFound() throws Exception {
        int itemId = 1;
        int ownerId = 1;

        when(itemService.getItemByOwner(eq(itemId), eq(ownerId)))
                .thenReturn(testItemForOwnerDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", "1")
                        .param("ownerId", String.valueOf(ownerId)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("A powerful laptop for work"));
    }


    @Test
    void addItem_ShouldCreateNewItem() throws Exception {
        when(itemService.addItem(any(ItemDto.class)))
                .thenReturn(testItem);

        String jsonRequest = objectMapper.writeValueAsString(testItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void patchItem_ShouldUpdateItemSuccessfully() throws Exception {
        // Given — готовим обновлённые данные
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(1);
        updatedItemDto.setName("Updated Laptop");
        updatedItemDto.setAvailable(false);
        updatedItemDto.setDescription("A powerful laptop for work"); // если нужно
        updatedItemDto.setOwner(1);
        Item updatedItem = new Item(1, "Updated Laptop", "A powerful laptop for work", false, testUser);

        when(itemService.patchItem(eq(updatedItemDto), eq(1)))
                .thenReturn(updatedItem);
        System.out.println("updatedItem==== " + updatedItem);
        String jsonRequest = objectMapper.writeValueAsString(updatedItemDto);
        System.out.println("jsonRequest==== " + jsonRequest);
        // When & Then — обновляем и проверяем результат
        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .param("ownerId", "1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.available").value(false));
    }

    /**
     * Тест поиска доступных предметов по тексту
     */
    @Test
    void searchAvailableItems_ShouldReturnFilteredItems() throws Exception {
        // Given — настраиваем мок, чтобы он возвращал список предметов
        List<Item> availableItems = List.of(
                new Item(2, "Book", "Interesting book", true, testUser),
                new Item(3, "Phone", "Smartphone", true, testUser)
        );

        when(itemService.searchAvailableItems(eq("Book")))
                .thenReturn(availableItems);

        // ищем и проверяем результаты
        mockMvc.perform(get("/items/search")
                        .param("text", "Book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].name", Matchers.hasItem("Book")));

    }

    /**
     * Тест получения всех предметов пользователя
     */
    @Test
    void getAllItemsForOwner_ShouldReturnUsersItems() throws Exception {
        // Given — готовим ожидаемые данные
        List<Item> userItems = List.of(testItem,
                new Item(2, "Tablet", "Portable tablet", true, testUser));

        when(itemService.getAllItemsForOwner(eq(1)))
                .thenReturn(userItems);

        // When — выполняем запрос
        mockMvc.perform(get("/items") // ← исправлен URL: убираем /owner/
                        .header("X-Sharer-User-Id", "1")) // ← передаём заголовок, как в контроллере

                // Then — проверяем результаты
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2)); // ← исправлено: length → size()
    }

    /**
     * Тест добавления комментария
     */
    @Test
    void addComment_ShouldAddCommentSuccessfully() throws Exception {
        // Given
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setText("This item is great!");

        Comment savedComment = new Comment();
        savedComment.setId(1);
        savedComment.setText("This item is great!");
        savedComment.setItem(testItem);
        savedComment.setAuthorName("Test User");
        savedComment.setCreated(Instant.now());

        when(itemService.addComment(eq(1), any(CommentRequest.class), eq(1)))
                .thenReturn(savedComment);
        System.out.println("savedComment============== " + savedComment);
        String jsonRequest = objectMapper.writeValueAsString(commentRequest);
        System.out.println("commentRequest============== " + commentRequest);
        // When & Then — добавляем комментарий
        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .param("authorId", "1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("This item is great!"));
    }

    @Test
    void addItem_ShouldReturnBadRequestWhenValidationFails() throws Exception {
        testItemDto.setName(""); // Некорректное имя

        when(itemService.addItem(any(ItemDto.class)))
                .thenThrow(new ValidationException("Некорректно заполнены поля itemDto"));

        String jsonRequest = objectMapper.writeValueAsString(testItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))

                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemByOwner_ShouldReturnNotFoundWhenItemDoesNotExist() throws Exception {
        // Given — настраиваем мок, чтобы он выбросил NotFoundException
        when(itemService.getItemByOwner(eq(999), eq(1)))
                .thenThrow(new NotFoundException("Item не найден"));

        // When & Then — проверяем обработку ошибки
        mockMvc.perform(get("/items/{itemId}", 999)
                        .header("X-Sharer-User-Id", "1")
                        .param("ownerId", "1"))


                .andExpect(status().isNotFound());
    }
}