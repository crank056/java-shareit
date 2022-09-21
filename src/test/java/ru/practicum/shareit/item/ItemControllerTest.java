package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    @Autowired
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    @Autowired
    private MockMvc mvc;
    private ItemDto itemDto;
    private ItemBookingDto itemBookingDto;
    private CommentDto commentDto;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemController).build();
        itemDto = new ItemDto(1L, "name", "desc", true, null, null);
        itemBookingDto = new ItemBookingDto(
                1L, "name", "desc",
                true, null, null,
                null, null, null);
        commentDto = new CommentDto(null, "text", null, null, null);
    }

    @Test
    void addItemTest() throws Exception{
        when(itemService.addItem(any(), anyLong())).thenReturn(itemDto);
        mvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.addComment(any(), anyLong(), anyLong())).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void refreshItemTest() throws Exception {
        when(itemService.refreshItem(any(), anyLong(), anyLong())).thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void getItemFromIdTest() throws Exception {
        when(itemService.getItemFromId(anyLong(), anyLong())).thenReturn(itemBookingDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemBookingDto.getName())))
                .andExpect(jsonPath("$.description", is(itemBookingDto.getDescription())))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void getAllItemFromUserIdTest() throws Exception {
        when(itemService.getAllItemsFromUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemBookingDto));

        mvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "1")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.[0].id", is(itemBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemBookingDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemBookingDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(true)));
    }

    @Test
    void getItemsFromKeyWord() throws Exception {
        when(itemService.getItemsFromKeyWord(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=text")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "1")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(true)));
    }
}
