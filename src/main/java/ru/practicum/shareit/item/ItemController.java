package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NullItemFieldException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.Map;

/**
 * // TODO .
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private ItemServiceImpl itemService;

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long id) throws WrongIdException {
        log.info("Получен запрос POST, объект: {}", itemDto);
        return ItemMapper.toItemDto(itemService.addItem(itemDto, id));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNullEmailException(final WrongIdException e) {
        return Map.of("Пользователь вещи отсутствует", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNullEmailException(final NullItemFieldException e) {
        return Map.of("Пользователь вещи отсутствует", e.getMessage());
    }


}
