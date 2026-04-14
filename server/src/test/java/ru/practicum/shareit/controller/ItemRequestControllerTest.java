package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForGetList;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    // Тестовые данные
    private User testUser;
    private ItemRequestDto testItemRequestDto;
    private ItemRequestDtoForGetList testItemRequestForGet;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых объектов
        testUser = new User(1, "Test User", "test@example.com");
        testItemRequestDto = new ItemRequestDto();
        testItemRequestDto.setDescription("Test Request");

        testItemRequestForGet = new ItemRequestDtoForGetList(
                1,
                "Test Request",
                testUser,
                LocalDateTime.now(),
                List.of()
        );
    }

    @Test
    void createItemRequest_ShouldCreateNewRequest() throws Exception {
        // 1. Вызываем мок‑сервис и сохраняем результат в переменную
        ItemRequest createdRequest = ItemRequestMapper.toItemRequest(testItemRequestDto, testUser);
        createdRequest.setId(1); // устанавливаем id вручную

        when(itemRequestService.addItemRequest(any(ItemRequestDto.class), eq(1)))
                .thenReturn(createdRequest);

        String jsonRequest = objectMapper.writeValueAsString(testItemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))

                // 2. Используем сохранённый id в проверке
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdRequest.getId()))
                .andExpect(jsonPath("$.description").value("Test Request"));
    }

    @Test
    void getAllItemRequests_ShouldReturnUserRequests() throws Exception {
        List<ItemRequestDtoForGetList> userRequests = List.of(testItemRequestForGet);

        when(itemRequestService.getAllUserRequests(eq(1)))
                .thenReturn(userRequests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Test Request"));
    }

    @Test
    void getItemRequestByRequestId_ShouldReturnRequest() throws Exception {
        when(itemRequestService.itemRequestDtoForGetListById(eq(1)))
                .thenReturn(testItemRequestForGet);

        mockMvc.perform(get("/requests/{requestId}", 1))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test Request"));
    }
}
