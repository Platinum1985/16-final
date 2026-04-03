package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByStartDesc(int userId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(int userId, Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(int userId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(int userId, Status status);

    Booking findByItemAndBooker(Item item, User booker);

    List<Booking> findByItem(Item item);
}
