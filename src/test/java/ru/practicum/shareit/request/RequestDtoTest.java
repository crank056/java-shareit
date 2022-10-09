package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.time.LocalDateTime;

@JsonTest
public class RequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "desc",
                1L,
                LocalDateTime.now(),
                null
        );
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        Assertions.assertThat(result).extractingJsonPathNumberValue(
                "$.id").isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue(
                "$.description").isEqualTo("desc");
        Assertions.assertThat(result).extractingJsonPathNumberValue(
                "$.requesterId").isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue(
                "$.created").isNotNull();
    }
}
