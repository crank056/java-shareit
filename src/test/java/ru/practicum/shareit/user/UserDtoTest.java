package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.Repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.userStorage.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ExtendWith(MockitoExtension.class)
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    public void testUserDto() throws Exception {
        UserDto userDto = new UserDto(
                1l,
                "name",
                "email@ya.ru"
        );
        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email@ya.ru");
    }
}
