package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.booking.IndicatorBooking;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Util;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Util.getElementsFrom;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;

    private final CommentMapper commentMapper;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("При добавлении вещи не найден пользователь под id = " + userId));

        Item newItem = itemRepository.save(
                itemMapper.toItem(itemDto).toBuilder().owner(user).build()
        );

        log.info("Добавлена вещь {}",newItem);

        return itemMapper.toItemDto(newItem);
    }


    @Override
    @Transactional
    public ItemDto upItem(ItemDto itemDto, long itemId, long userId) {

        if (userId <= 0) {
            throw new BadRequestException("Не коректный id пользователя = " + userId);
        }

        if (itemDto.getOwner().getId() != userId) {
            throw new NotFoundException("Не владелец этой вещи пользователь под id " + userId);
        }

        Item updateItem = itemRepository.save(itemMapper.toItem(itemDto));

        log.info("Обновлена вещь {}",updateItem);

        return itemMapper.toItemDto(updateItem);
    }

    @Override
    public ItemDto getItem(long itemId, long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь под id = " + itemId));

        log.info("Вернулась вещь {}, пользователя {}", item, userId);

        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId, int from, int size) {

        log.info("Вернуть все вещи пользователя {}", userId);

        Sort sortById = Sort.by(Sort.Direction.ASC,"id");

        Pageable page = Util.createPageParam(from,size,sortById);

        List<Item> itemList = getElementsFrom(itemRepository.findByOwnerId(userId,page),Util.start(from,size));

        List<Long> itemsId = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());

        List<Booking> bookingList = bookingRepository.findByItemsIdBooking(itemsId,Status.APPROVED);

        Map<Long,List<Booking>> itemsBookingMap = bookingList.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        return itemList.stream().map(item -> {
            long itemId = item.getId();
            if (itemsBookingMap.containsKey(itemId)) {
                List<IndicatorBooking> indicatorBookingList = setIndicatorBooking(
                        itemsBookingMap.get(itemId)
                );

            log.info("При возврате всех вещей пользователя. " +
                    "Бронирования предыдущий и следующий, всего штук {}!", indicatorBookingList.size());

            return itemMapper.toItemDto(item).toBuilder()
                    .lastBooking(indicatorBookingList.get(0))
                    .nextBooking(indicatorBookingList.get(1))
                    .build();
            }
        return itemMapper.toItemDto(item);
        }).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemByRequestUsers(long itemId, long userIdMakesRequest) {

        if (!userRepository.existsById(userIdMakesRequest)) {
           throw new NotFoundException(
                   "Не найден пользователь # при запросе вещи # пользователем #",
                   userIdMakesRequest,
                   itemId,
                   userIdMakesRequest
           );
        }

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Не найдена вещь под id = " + itemId)
        );

        List<IndicatorBooking> indicatorBookingList = setIndicatorBooking(
                bookingRepository
                        .findByItemIdAndItemOwnerIdAndStatusOrderByStartAsc(itemId,userIdMakesRequest,Status.APPROVED)
        );

        log.info("Бронирования предыдущий и следующий, всего штук {}", indicatorBookingList.size());

        List<CommentDto> commentsDto = commentMapper.toCommentDtoList(
                commentRepository.findByItemId(itemId)
        );

        log.info("Вернулась вещь {} по запросу пользователя {}", item, userIdMakesRequest);

        return itemMapper.toItemDto(item).toBuilder()
                    .lastBooking(indicatorBookingList.get(0))
                    .nextBooking(indicatorBookingList.get(1))
                    .comments(commentsDto)
                    .build();

    }

    @Override
    public List<ItemDto> search(String text, long userId, int from, int size) {

        log.info("Поиск вещей по тексту {}, по запросу пользователя {}", text, userId);

        if (text.isBlank()) {
        return new ArrayList<>(0);
        }

        Pageable page = Util.createPageParam(from,size);

        return getElementsFrom(
                itemRepository.searchByIgnoreCaseDescriptionContainingAndAvailableTrue(text,page).stream()
                .map(item -> itemMapper.toItemDto(item))
                .collect(Collectors.toList()), Util.start(from,size)
        );
    }


    public CommentDto addComment(CreateCommentDto createCommentDto, long itemId, long authorId) {

        if (!bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndBefore(itemId,authorId,Status.APPROVED,LocalDateTime.now())) {
          throw new BadRequestException("У пользователя # не было вещи # в аренде!",itemId,authorId);
        }

        Comment comment = Comment.builder()
                .text(createCommentDto.getText())
                .item(itemRepository.findById(itemId).orElseThrow(
                        () -> new NotFoundException("Не найдена вещь #, при добавлении комментария",itemId)))
                .author(userRepository.findById(authorId).orElseThrow(
                        () -> new NotFoundException("Не найден пользователь #, при добавлении комментария",authorId)))
                .created(LocalDateTime.now())
                .build();

        Comment newComment = commentRepository.save(comment);

        log.info("Добавлен комментарий вещи {} пользователем {}!",itemId,authorId);

        return commentMapper.toCommentDto(newComment);
    }

    private List<IndicatorBooking> setIndicatorBooking(List<Booking> bookingsList) {

        IndicatorBooking lastBooking = null;
        IndicatorBooking nextBooking = null;
        List<IndicatorBooking> indicatorBookingList = new ArrayList<>();

        if (bookingsList == null) {
            return indicatorBookingList;
        }

        bookingsList.sort((b1,b2) -> b1.getStart().compareTo(b2.getStart()));

        int size = bookingsList.size();

        if (size == 1) {
            Booking bookingLast = bookingsList.get(0);
            lastBooking = new IndicatorBooking(bookingLast.getId(),bookingLast.getBooker().getId());
            indicatorBookingList.add(lastBooking);
            indicatorBookingList.add(nextBooking);
            return indicatorBookingList;
        }

        if (size == 2 || size == 3) {
            Booking bookingLast = bookingsList.get(0);
            Booking bookingNext = bookingsList.get(1);
            lastBooking = new IndicatorBooking(bookingLast.getId(),bookingLast.getBooker().getId());
            nextBooking = new IndicatorBooking(bookingNext.getId(),bookingNext.getBooker().getId());
            indicatorBookingList.add(lastBooking);
            indicatorBookingList.add(nextBooking);
            return indicatorBookingList;
        }

        if (size > 2) {
            Booking bookingLast = bookingsList.get(1);
            Booking bookingNext = bookingsList.get(size - 2);
            lastBooking = new IndicatorBooking(bookingLast.getId(),bookingLast.getBooker().getId());
            nextBooking = new IndicatorBooking(bookingNext.getId(),bookingNext.getBooker().getId());
            indicatorBookingList.add(lastBooking);
            indicatorBookingList.add(nextBooking);
            return indicatorBookingList;
        }

        indicatorBookingList.add(lastBooking);
        indicatorBookingList.add(nextBooking);
        return indicatorBookingList;

    }
}

