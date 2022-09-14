package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

public class RequestMapper {

    public static ItemRequestDto ToDto(ItemRequest itemRequest, User requester) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                requester,
                itemRequest.getCreated()
        );
    }

    public static ItemRequest toRequest(ItemRequestDto itemRequestDto, User requester) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requester,
                itemRequestDto.getCreated()
        );
    }
}
