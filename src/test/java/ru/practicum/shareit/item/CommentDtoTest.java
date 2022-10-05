package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testItemDto() throws Exception {
        CommentDto commentDto = new CommentDto(
                1l,
                "text",
                null,
                "name",
                LocalDateTime.now()
        );
        JsonContent<CommentDto> result = json.write(commentDto);
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("name");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.created").isNotNull();
    }
}
