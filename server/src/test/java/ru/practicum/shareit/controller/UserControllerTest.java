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
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1, "User name", "user@gmail.com");
    }

   /* @Test
    void findAll_shouldReturnUsers() throws Exception {
        when(userService.getAll()).thenReturn(Collections.singleton(userDto));
        mockMvc.perform(get("/users"))
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));
    } */

    @Test
    void findById_shouldReturnUser() throws Exception {
        when(userService.getUserById(eq(user.getId()))).thenReturn(user);

        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void findById_shouldThrowNotFoundException_whenUserDoesNotExists() throws Exception {
        int userId = user.getId();
        String exMessage = format("User по id=%d не найден", userId);
        when(userService.getUserById(eq(userId))).thenThrow(new NotFoundException(exMessage));
        mockMvc.perform(get("/users/" + userId)).andExpect(status().isNotFound()).andExpect(jsonPath("$.error").value(exMessage));
    }

    @Test
    void createUser_userShouldBeCreated() throws Exception {
        UserDto requestUserDto = new UserDto("User name", "user@gmail.com");
        when(userService.createUser(any(UserDto.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto)))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void createUser_shouldThrowUserEmailIsNotUnique_whenEmailIsNotValid() throws Exception {
        UserDto requestUserDto = new UserDto("User name", "user@gmail.com");
        String exMessage = "Пользователь с таким email уже существует";

        when(userService.createUser(any(UserDto.class)))
                .thenThrow(new DuplicateEmailException(exMessage));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto)))
                .andExpect(status().isConflict()) // 409 — Conflict
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.['Duplicate email']").value("Пользователь с таким email уже существует")); // Изменено здесь
    }

    @Test
    void updateUser_userShouldBeCreated() throws Exception {
        UserDto requestUserDto = new UserDto("User name", "user@gmail.com");
        int userId = 1;

        when(userService.updateUser(any(UserDto.class), eq(userId))).thenReturn(user);

        mockMvc.perform(patch(format("/users/%d", userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto)))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void updateUser_shouldThrowUserEmailIsNotUnique_whenEmailIsNotValid() throws Exception {
        UserDto requestUserDto = new UserDto("User name", "user@gmail.com");
        int userId = 1;
        String exMessage = "Пользователь с таким email уже существует";

        when(userService.updateUser(any(UserDto.class), eq(userId)))
                .thenThrow(new DuplicateEmailException(exMessage));

        mockMvc.perform(patch(format("/users/%d", userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto)))
                .andExpect(jsonPath("$.['Duplicate email']").value("Пользователь с таким email уже существует"));
    }

    @Test
    void deleteUser_userShouldBeDeleted() throws Exception {
        int userId = 1;

        // Настраиваем мок: сервис должен успешно удалить пользователя
        doNothing().when(userService).deleteUser(eq(userId));

        // Выполняем удаление
        mockMvc.perform(delete(format("/users/%d", userId)))
                .andExpect(status().isOk());

        // Проверяем, что пользователь удалён — GET-запрос возвращает 404
        mockMvc.perform(get(format("/users/%d", userId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldReturnNotFoundException() throws Exception {
        int userId = 1;
        String exMessage = format("User по id=%d не найден", userId);

        doThrow(new NotFoundException(exMessage)).when(userService).deleteUser(userId);

        mockMvc.perform(delete(format("/users/%d", userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User по id=1 не найден"));
    }
}
