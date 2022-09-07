package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getOwner(),
                item.getRequest() != null ? item.getRequest() : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest() != null ? itemDto.getRequest() : null
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
                item.getOwner(),
                item.getRequest() != null ? item.getRequest() : null,
                last,
                next,
                comments
        );
    }
}
