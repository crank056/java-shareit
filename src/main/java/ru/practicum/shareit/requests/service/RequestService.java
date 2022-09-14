package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getRequest(Long userId);

    List<ItemRequestDto> getAllWithPagination(Integer from, Integer size);

    ItemRequestDto getRequestFromId(Long requestId);
}
