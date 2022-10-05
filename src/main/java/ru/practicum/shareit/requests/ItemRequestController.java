package ru.practicum.shareit.requests;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.RequestService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestService requestService;

    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getAllWithPage(@RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size) {
        return requestService.getAllWithPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestFromId(@RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) throws WrongIdException {
        return requestService.getRequestFromId(userId, requestId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidateException(final ValidationException e) {
        return Map.of("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNullEmailException(final WrongIdException e) {
        return Map.of("Пользователь отсутствует", e.getMessage());
    }
}
