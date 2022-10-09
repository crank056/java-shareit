package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b, Item i " +
            "WHERE b.item.id = i.id AND b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(@Param("bookerId") Long bookerId, Pageable page);

    @Query("SELECT b FROM Booking b, Item i " +
            "WHERE b.item.id = i.id AND b.booker.id = :bookerId AND b.start < :start AND b.end > :end " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdInCurrent(
            @Param("bookerId") Long bookerId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User booker, LocalDateTime end);

    @Query("SELECT b FROM Booking b, Item i " +
            "WHERE b.item.id = i.id AND b.booker.id = :bookerId AND b.end < :end " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdInPastWithPage(
            @Param("bookerId") Long bookerId,
            @Param("end") LocalDateTime end, Pageable page);

    @Query("SELECT b FROM Booking b, Item i " +
            "WHERE b.item.id = i.id AND b.booker.id = :bookerId AND b.start > :start AND b.end > :end " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdInFuture(
            @Param("bookerId") Long bookerId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end, Pageable page);

    @Query("SELECT b FROM Booking b, Item i " +
            "WHERE b.item.id = i.id AND b.booker.id = :bookerId AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdAndStatus(
            @Param("bookerId") Long bookerId,
            @Param("status") Status status, Pageable page);

    @Query("SELECT b FROM Booking b, Item i " +
            "WHERE b.item.id = i.id AND i.owner = :owner AND b.start < :start AND b.end > :end " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerInCurrent(
            @Param("owner") User owner,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end, Pageable page);

    @Query("SELECT b FROM Booking b, Item i " +
            "WHERE b.item.id = i.id AND i.owner = :owner AND b.end < :end " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerInPastWithPage(
            @Param("owner") User owner,
            @Param("end") LocalDateTime end, Pageable page);

    @Query("SELECT b FROM Booking b, Item i " +
            "WHERE b.item.id = i.id AND i.owner = :owner AND b.start > :start AND b.end > :end " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerInFuture(
            @Param("owner") User owner,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end, Pageable page);

    @Query("SELECT b FROM Booking b, Item i " +
            "WHERE b.item.id = i.id AND i.owner = :owner AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerAndStatus(
            @Param("owner") User owner, @Param("status") Status status, Pageable page);

    @Query("SELECT b FROM Booking b, Item i" +
            " WHERE b.item.id = i.id AND b.item = :item " +
            "ORDER BY b.start ASC")
    List<Booking> findAllByItem(@Param("item") Item item);

    @Query("SELECT b FROM Booking b, Item i " +
            "WHERE b.item.id = i.id AND i.owner = :owner " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByOwner(@Param("owner") User owner, Pageable page);
}
