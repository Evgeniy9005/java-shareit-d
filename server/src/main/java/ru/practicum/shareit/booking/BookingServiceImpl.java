package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.util.Util.*;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Booking addBooking(CreateBooking createBooking, Long userId) {

    validDate(createBooking);

    Item item = itemRepository.findById(createBooking.getItemId())
            .orElseThrow(() -> new NotFoundException("Не найдена, при бронировании вещь!"));

    if (item.getOwner().getId() == userId) {
        throw new NotFoundException("Не может владелец вещи создать бронь на свою вещь!");
    }

        if (!item.isAvailable()) {
            throw new BadRequestException("Вещь # уже забронирована!",createBooking.getItemId());
        }

    Booking booking = Booking.builder()
            .start(createBooking.getStart())
            .end(createBooking.getEnd())
            .item(item)
            .booker(userRepository.findById(userId).orElseThrow(
                    () -> new NotFoundException("Не найден, при бронировании пользователь " + userId)))
            .status(Status.WAITING)
            .build();

        Booking newBooking = bookingRepository.save(booking);

        log.info("Создано бронирование {}",newBooking);

        return newBooking;
    }

    @Override
    @Transactional
    public Booking setStatus(long bookingId, long userId, Boolean approved) {

        if (approved == null) {
            throw new BadRequestException("Не определен статус одобрения вещи на бронирование!");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование под id = " + bookingId));

        Booking setStatusBooking = null;

        long owner = booking.getItem().getOwner().getId();
        long booker = booking.getBooker().getId();

        if (owner == userId) { //если userId владелец вещи
            if (approved) { //и approved = true
                if (booking.getStatus().equals(Status.APPROVED)) {
                    throw new BadRequestException(
                            "Бронирование вещи # уже подтверждено владельцем #!",booking.getItem().getId(),owner
                    );
                }
               setStatusBooking = booking.toBuilder().status(Status.APPROVED).build();//подтверждение бронирования владельцем вещи
            } else { //и approved = false
               setStatusBooking = booking.toBuilder().status(Status.REJECTED).build();//отклонение бронирования владельцем вещи
            }
        }

        if (booker == userId) { //если userId бронирующий вещь
            if (approved) {
                throw new NotFoundException(
                        "Бронирующий # может только отменить бронь!",userId
                );
            } else { //и approved = false
                setStatusBooking = booking.toBuilder().status(Status.CANCELED).build();//отклонение бронирования заказчиком
            }
        }

        log.info("Бронь {} до обновления, статус бронирования {} ",bookingId,setStatusBooking.getStatus());

        Booking upBooking = bookingRepository.save(setStatusBooking);

        log.info("Обновлен статус бронирования {}",upBooking);

        return upBooking;
    }


    @Override
    public Booking getBookingByIdForUserId(long bookingId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь # при запросе брони # вещи",userId,bookingId);
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование под id = " + bookingId));


        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new NotFoundException(
                    "Не найден владелец # вещи или заказчик # в бронировании # ", userId, userId, booking.getId());
        }

        log.info("Бронирование отправлено {} ",booking);
        return booking;
    }


    @Override
    public List<Booking> getBookingsForBooker(long userId, String state, int from, int size) { //для пользователя
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь # при запросе всех бронирований вещей", userId);
        }

        Pageable page = validPageParam(from,size);

        int start = start(from,size);

        List<Booking> bookingList;
        switch (state) {
            case "ALL":
                bookingList = getElementsFrom(bookingRepository.findByBookerIdOrderByIdDesc(userId,page),start);
                log.info("Вернулись брони вещей в количестве = {}, c параметром выборки {}", bookingList.size(), state);
                return bookingList;
            case "FUTURE":

                bookingList = getElementsFrom(bookingRepository.findByBookerIdOrderByStartDesc(userId,page),start);
                log.info("Вернулись брони вещей в количестве = {}, c параметром выборки {}", bookingList.size(), state);
                return bookingList;
            case "WAITING":
                bookingList = getElementsFrom(
                        bookingRepository.findByBookerIdAndStatusOrderByIdDesc(userId,Status.WAITING,page),start);
                log.info("Вернулись брони вещей в количестве = {}, c параметром выборки {}", bookingList.size(), state);
                return bookingList;
            case "REJECTED":
                bookingList = getElementsFrom(
                        bookingRepository.findByBookerIdAndStatusOrderByIdDesc(userId,Status.REJECTED,page),start);
                log.info("Вернулись брони вещей в количестве = {}, c параметром выборки {}", bookingList.size(), state);
                return bookingList;
            case "CURRENT":
                bookingList = getElementsFrom(
                        bookingRepository.findByBookingCurrentForBooker(userId,LocalDateTime.now(),page),start);
                log.info("Вернулись брони вещей в количестве = {}, c параметром выборки {}", bookingList.size(), state);
                return bookingList;
            case "PAST":
                bookingList = getElementsFrom(
                        bookingRepository.findByBookerIdAndEndBeforeOrderByEndDesc(userId,LocalDateTime.now(),page),
                        start
                );
                log.info("Вернулись брони вещей в количестве = {}, c параметром выборки {}", bookingList.size(), state);
                return bookingList;
            case "UNSUPPORTED_STATUS":
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            default:
                bookingList = getElementsFrom(bookingRepository.findByBookerIdOrderByStartDesc(userId,page), start);
                log.info("Вернулись брони вещей в количестве = {}, по умолчанию!", bookingList.size());
                return bookingList;
        }

    }


    @Override
    public List<Booking> getBookingsOwnerState(long userId, String state, int from, int size) { //для владельца бронирований
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь # при запросе бронирований этим пользователем", userId);
        }

        Pageable page = validPageParam(from,size);

        int start = start(from,size);

        List<Booking> bookingOwnerList;
        switch (state) {
            case "ALL":
                bookingOwnerList = getElementsFrom(
                        bookingRepository.findByItemOwnerIdOrderByIdDesc(userId,page),start);
                log.info("Вернулись брони вещей в количестве {}, запрошенных владельцем {} броней, с ",
                        bookingOwnerList.size(),userId);
                return bookingOwnerList;
            case "FUTURE":
                bookingOwnerList = getElementsFrom(
                        bookingRepository.findByItemOwnerIdOrderByStartDesc(userId,page),start);
                log.info("Вернулись брони вещей в количестве {}, запрошенных владельцем {} броней ",
                        bookingOwnerList.size(),userId);
                return bookingOwnerList;
            case "WAITING":
                bookingOwnerList = getElementsFrom(
                        bookingRepository.findByItemOwnerIdAndStatusOrderByIdDesc(userId,Status.WAITING,page),start);
                log.info("Вернулись брони вещей в количестве {}, " +
                        "запрошенных владельцем {} броней со статусом WAITING",bookingOwnerList.size(),userId);
                return bookingOwnerList;
            case "REJECTED":
                bookingOwnerList = getElementsFrom(
                        bookingRepository.findByItemOwnerIdAndStatusOrderByIdDesc(userId,Status.REJECTED,page),start);
                log.info("Вернулись брони вещей в количестве {}, " +
                        "запрошенных владельцем {} броней со статусом REJECTED",bookingOwnerList.size(),userId);
                return bookingOwnerList;
            case "CURRENT":
                bookingOwnerList = getElementsFrom(
                        bookingRepository.findByBookingCurrentForOwner(userId,LocalDateTime.now(),page),start);
                log.info("Вернулись брони вещей в количестве {}, " +
                        "запрошенных владельцем {} броней со статусом CURRENT",bookingOwnerList.size(),userId);
                return bookingOwnerList;
            case "PAST":
                bookingOwnerList = getElementsFrom(bookingRepository
                        .findByItemOwnerIdAndEndBeforeOrderByEndDesc(userId,LocalDateTime.now(),page),start);
                log.info("Вернулись брони вещей в количестве {}, " +
                        "запрошенных владельцем {} броней со статусом PAST",bookingOwnerList.size(),userId);
                return bookingOwnerList;
            case "UNSUPPORTED_STATUS":
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            default:
                bookingOwnerList = getElementsFrom(
                        bookingRepository.findByItemOwnerIdOrderByStartDesc(userId,page),start
                );
                log.info("Вернулись брони вещей в количестве {}, запрошенных владельцем {} броней, по умолчанию",
                        bookingOwnerList.size(),userId);
                return bookingOwnerList;

        }
    }

    private void validDate(CreateBooking createBooking) {
        LocalDateTime start = createBooking.getStart();
        LocalDateTime end = createBooking.getEnd();

        int equals = start.compareTo(end);

        if (equals == 0) {
            throw new BadRequestException(
                    "Время начала # бронирования не может быть равно времени окончания #",start,end
            );
        }

        if (equals > 0) {
            throw new BadRequestException(
                    "Время начала # бронирования не может быть позже времени окончания #",start,end
            );
        }

        if (start.compareTo(LocalDateTime.now()) < 0) {
            throw new BadRequestException("Время начала # бронирования не может быть в прошлом!",start);
        }
    }
}
