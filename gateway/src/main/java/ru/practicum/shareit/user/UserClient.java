package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;


public class UserClient extends BaseClient {

    @Autowired
    public UserClient(@Qualifier("itemRestTemplate") RestTemplate restTemplate) {
        super(restTemplate); // Передаём уже настроенный RestTemplate
    }

    public ResponseEntity<Object> getUser(Integer userId) {
        return get("/" + userId, userId);
    }

    // Исправление: используем метод post с одним аргументом path и body
    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("", userDto, 0);
    }

    public ResponseEntity<Object> updateUser(Integer userId, UserDto userDto) {
        return patch("/" + userId, userDto, userId);
    }

    public ResponseEntity<Object> deleteUser(Integer userId) {
        return delete("/" + userId, userId);
    }
}