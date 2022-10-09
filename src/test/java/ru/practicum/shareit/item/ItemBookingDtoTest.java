package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemBookingDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemBookingDtoTest {
    @Autowired
    private JacksonTester<ItemBookingDto> json;

    @Test
    void testItemDto() throws Exception {
        ItemBookingDto itemBookingDto = new ItemBookingDto(
                1L,
                "name",
                "description",
                true,
                null,
                null,
                null,
                null,
                null
        );
        JsonContent<ItemBookingDto> result = json.write(itemBookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }
}
