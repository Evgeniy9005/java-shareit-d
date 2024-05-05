package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> findByItemIdAndItemOwnerIdAndStatusOrderByStartAsc(long itemId,long ownerId, Status approved);

    //наличие вещи в аренде
    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
            long itemId, long bookerId, Status approved, LocalDateTime end);

    List<Booking> findByBookerIdOrderByIdDesc(long bookerId, Pageable pageable);

    List<Booking> findByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByIdDesc(long ownerId,Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByIdDesc(long bookerId, Status state, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start <= ?2 and b.end >= ?2 order by b.id asc")
    List<Booking> findByBookingCurrentForBooker(long bookerId, LocalDateTime current, Pageable pageable);

    List<Booking> findByBookerIdAndEndBeforeOrderByEndDesc(long bookerId, LocalDateTime past, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatusOrderByIdDesc(long ownerId, Status state,Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start <= ?2 and b.end >= ?2 order by b.id asc")
    List<Booking> findByBookingCurrentForOwner(long ownerId, LocalDateTime current, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByEndDesc(long ownerId, LocalDateTime past, Pageable pageable);

    @Query("select b from Booking b where b.item.id in(:itemsId) and b.status = :status")
    List<Booking> findByItemsIdBooking(@Param("itemsId") List<Long> itemsId, @Param("status") Status status);

}
