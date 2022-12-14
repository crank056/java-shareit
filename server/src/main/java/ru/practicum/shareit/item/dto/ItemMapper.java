package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getOwner().getId(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest
        );
    }

    public static ItemBookingDto toItemBookingDto(
            Item item,
            BookingItemDto last,
            BookingItemDto next,
            List<CommentDto> comments) {
        return new ItemBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getOwner().getId(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                last,
                next,
                comments
        );
    }
}
