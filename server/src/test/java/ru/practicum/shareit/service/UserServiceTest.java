package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;
    private final UserRepository userRepository;

    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userDto = new UserDto("User-1", "user@gmail.com");
    }

    @Test
    public void create_userShouldBeCreated() {
        User savedUser = userService.createUser(userDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo(userDto.getName());
        assertThat(savedUser.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    public void updateUser_shouldUpdateUserSuccessfully() {
        // Создаём пользователя
        User savedUser = userService.createUser(userDto);

        // Подготавливаем данные для обновления
        UserDto updatedUserDto = new UserDto("Updated-User", "updated@gmail.com");

        // Обновляем пользователя
        User updatedUser = userService.updateUser(updatedUserDto, savedUser.getId());

        // Проверяем, что данные обновились
        assertThat(updatedUser.getName()).isEqualTo("Updated-User");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@gmail.com");
    }

    @Test
    public void updateUser_shouldThrowValidationExceptionOnInvalidEmail() {
        User savedUser = userService.createUser(userDto); // создаём тестового пользователя

        UserDto invalidEmailUserDto = new UserDto("User-2", "invalid-email"); // email без @

        assertThatThrownBy(() -> userService.updateUser(invalidEmailUserDto, savedUser.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessage("email должен содержать @");
    }

    @Test
    public void deleteUser_shouldDeleteUserSuccessfully() {
        // Создаём пользователя
        User savedUser = userService.createUser(userDto);

        // Удаляем пользователя
        userService.deleteUser(savedUser.getId());

        assertThat(userRepository.existsById(savedUser.getId()))
                .isFalse(); // Проверяем, что пользователь удалён
    }

    @Test
    public void deleteUser_throwsExceptionOnNonExistingUser() {
        assertThatThrownBy(() -> userService.deleteUser(-1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User с таким id не найден");
    }

    @Test
    public void getUserById_shouldGetUserSuccessfully() {
        // Создаём пользователя
        User savedUser = userService.createUser(userDto);

        // Получаем пользователя по ID
        User retrievedUser = userService.getUserById(savedUser.getId());

        // Проверяем, что данные совпадают
        assertThat(retrievedUser.getName()).isEqualTo(savedUser.getName());
        assertThat(retrievedUser.getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    public void getUserById_throwsExceptionOnNonExistingUser() {
        assertThatThrownBy(() -> userService.getUserById(-1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User с таким id не найден");
    }
}