package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status, Pageable pageable);

    List<Booking> findAllByItemOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(
            Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, Status status, Pageable pageable);

    List<Booking> findAllByItemId(Integer itemId);

    List<Booking> findAllByItemIdAndBookerIdAndStatusAndEndBefore(Integer itemId, Long authorId, Status approved, LocalDateTime now);
}
