package ru.practicum.shareit.item.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemBookingDto {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private User owner;
        private ItemRequest request;
        private Booking lastBooking;
        private Booking nextBooking;
}
