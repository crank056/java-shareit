package ru.practicum.shareit.requests;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.RequestService;

import java.util.List;

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
    List<ItemRequestDto> getAllWithPage(
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size) {
        return requestService.getAllWithPagination(from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestFromId(
            @PathVariable Long requestId) {
        return requestService.getRequestFromId(requestId);
    }
}
