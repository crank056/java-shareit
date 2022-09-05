package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(
            User owner,
            LocalDateTime start,
            LocalDateTime end);

    List<Booking> findAllByItemOwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime end);

    List<Booking> findAllByItemOwnerAndStartAfterAndEndAfterOrderByStartDesc(User owner, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(User owner, Status status);

    @Query("SELECT b FROM Booking b, Item i " +
            "WHERE b.item.id = i.id AND i.owner = :owner " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByOwner(@Param("owner") User owner);

    @Query("SELECT b FROM Booking b, Item i " +
            "WHERE b.item.id = i.id AND i.owner = :owner AND b.start > :date " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByOwnerFuture(@Param("owner") User owner, @Param("date") LocalDateTime date);

}
