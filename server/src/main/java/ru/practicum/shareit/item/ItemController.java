package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId)
            throws WrongIdException, ValidationException {
        log.info("Получен запрос POST, объект: {}", itemDto);
        return itemService.addItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody Comment comment,
                                 @PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId)
            throws WrongIdException, ValidationException, AccessException {
        log.info("Получен запрос POST, объект: {}", comment);
        return itemService.addComment(comment, itemId, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto refreshItem(@RequestBody ItemDto itemDto, @PathVariable Long id,
                               @RequestHeader("X-Sharer-User-Id") Long userId)
            throws WrongIdException, ValidationException {
        log.info("Получен запрос PATCH, объект: {}", itemDto);
        return itemService.refreshItem(itemDto, id, userId);
    }

    @GetMapping("/{id}")
    public ItemBookingDto getItemFromId(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id)
            throws WrongIdException {
        return itemService.getItemFromId(userId, id);
    }

    @GetMapping
    public List<ItemBookingDto> getAllItemsFromUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(required = false, defaultValue = "0") int from,
                                                      @RequestParam(required = false, defaultValue = "20") int size)
            throws WrongIdException, ValidationException {
        return itemService.getAllItemsFromUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsFromKeyWord(@RequestParam String text,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(required = false, defaultValue = "0") int from,
                                             @RequestParam(required = false, defaultValue = "20") int size) throws ValidationException {
        return itemService.getItemsFromKeyWord(text, from, size);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private Map<String, String> handleNullEmailException(final WrongIdException e) {
        return Map.of("Пользователь вещи отсутствует", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private Map<String, String> handleValidateException(final ValidationException e) {
        return Map.of("Вещь не прошла валидацию", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private Map<String, String> handleAccessException(final AccessException e) {
        return Map.of("Ошибка доступа", e.getMessage());
    }
}
