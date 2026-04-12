package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import ru.practicum.shareit.user.UserDto;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoTest {
    private final ObjectMapper objectMapper;

    @Test
    void testSerialization() throws Exception {
        UserDto requestUserDto = new UserDto("Иван", "ivan@example.com");
        String jsonString = objectMapper.writeValueAsString(requestUserDto);
        assertThat(jsonString).contains("Иван");
        assertThat(jsonString).contains("ivan@example.com");
    }
    @Test
    void testDeserialization() throws Exception {
        String jsonString = "{\"name\":\"Иван\", \"email\":\"ivan@example.com\"}";
        UserDto requestUserDto = objectMapper.readValue(jsonString, UserDto.class);
        assertThat(requestUserDto.getName()).isEqualTo("Иван");
        assertThat(requestUserDto.getEmail()).isEqualTo("ivan@example.com");
    }
}
