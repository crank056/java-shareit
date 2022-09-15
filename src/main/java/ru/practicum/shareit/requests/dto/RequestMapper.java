package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public class RequestMapper {

    public static ItemRequestDto ToDto(ItemRequest itemRequest, List<ItemDto> itemsDto) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated(),
                itemsDto
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
