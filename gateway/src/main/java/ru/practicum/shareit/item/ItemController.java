package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.item.dto.CommentDtoValid;
import ru.practicum.shareit.item.dto.ItemDtoValid;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.awt.print.Pageable;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestBody @Valid ItemDtoValid itemDtoValid) {
        return itemClient.addItem(userId, itemDtoValid);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid CommentDtoValid commentDtoValid,
                                             @PathVariable Long itemId) {
        return itemClient.addComment(userId, itemId, commentDtoValid);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> refreshItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestBody @Valid ItemDtoValid itemDtoValid,
                                              @PathVariable Long id) {
        return itemClient.refreshItem(userId, id, itemDtoValid);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemFromId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable Long id) {
        return itemClient.getItemFromId(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsFromUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getAllItemsFromUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsFromKeyWord(@RequestParam("text") String text,
                                                      @RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getItemsFromKeyWord(text, userId, from, size);
    }
}
