package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoTest {
    private final ObjectMapper objectMapper;

    @Test
    void testSerialization() throws Exception {
        ItemDto requestItemDto = new ItemDto(0, "Item Name", "Item Description", true, 1, 0);
        String jsonString = objectMapper.writeValueAsString(requestItemDto);
        assertThat(jsonString).contains(
                requestItemDto.getName(), requestItemDto.getDescription(),
                String.valueOf(requestItemDto.getAvailable()),
                String.valueOf(requestItemDto.getRequestId())
        );
    }

    @Test
    void testDeserialization() throws Exception {
        String jsonString = "{\"name\":\"Item Name\",\"description\":\"Item Description\",\"available\":true,\"requestId\":1}";

        ItemDto requestItemDto = objectMapper.readValue(jsonString, ItemDto.class);

        assertThat(requestItemDto.getName()).isEqualTo("Item Name");
        assertThat(requestItemDto.getDescription()).isEqualTo("Item Description");
        assertThat(requestItemDto.getAvailable()).isTrue();
        assertThat(requestItemDto.getRequestId()).isEqualTo(1);
    }
}
