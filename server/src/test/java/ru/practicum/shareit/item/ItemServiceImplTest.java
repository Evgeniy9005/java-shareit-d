package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.util.Util;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.data.Data.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private ItemMapper itemMapper = new ItemMapperImpl();

    private CommentMapper commentMapper = new CommentMapperImpl();

    private List<Booking> bookingList;

    private List<Item> itemList;

    private List<ItemDto> itemDtoList;

    private List<User> userList;


    @BeforeEach
    void start() {
        itemService = new ItemServiceImpl(
                itemMapper,
                commentMapper,
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository);

        userList = generationData(2, User.class);
        printList(userList,">>>");

        itemList = generationData(2, Item.class,userList.get(0),1L);
        itemDtoList = itemMapper.toItemDtoList(itemList);
        printList(itemDtoList,"===");

        bookingList = generationData(3, Booking.class,userList.get(0),itemList.get(0));
        printList(bookingList,"_+_");

    }

    @Test
    void addItem() {
        assertThrows(NotFoundException.class,() -> itemService.addItem(itemDtoList.get(0),1),
                "При добовлении вещи не найден пользователь под id = 1");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userList.get(0)));

        itemService.addItem(itemDtoList.get(0),1);

        verify(itemRepository).save(any(Item.class));
        verify(userRepository,times(2)).findById(anyLong());
    }

    @Test
    void upItem() {
        assertThrows(BadRequestException.class,() -> itemService.upItem(itemDtoList.get(0),1,-1),
                "Не коректный id пользователя = -1");

        assertThrows(NotFoundException.class,() -> itemService.upItem(itemDtoList.get(0),1,2),
                "Не владелец этой вещи пользователь под id 2");

        itemService.upItem(itemDtoList.get(0),1,1);

        verify(itemRepository).save(any(Item.class));

    }

    @Test
    void getItem() {
        assertThrows(NotFoundException.class,() -> itemService.getItem(1,1),
                "Не найдена вещь под id = 1");

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemList.get(0)));

        itemService.getItem(1,1);
        verify(itemRepository,times(2)).findById(anyLong());
    }

    @Test
    void getItemsByUserId() {

        when(itemRepository.findByOwnerId(anyLong(),any())).thenReturn(itemList);

        itemService.getItemsByUserId(1L,0,10);

        verify(itemRepository).findByOwnerId(anyLong(),any());
        verify(bookingRepository).findByItemsIdBooking(any(), any());

    }

    @Test
    void getItemByRequestUsers() {
        assertThrows(NotFoundException.class,() -> itemService.getItemByRequestUsers(1,1),
                "Не найден пользователь 1 при запросе вещи 1 пользователем 1");
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(NotFoundException.class,() -> itemService.getItemByRequestUsers(1,1),
                "Не найдена вешь под id = 1");

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemList.get(0)));

        itemService.getItemByRequestUsers(1,1);

        verify(bookingRepository)
                .findByItemIdAndItemOwnerIdAndStatusOrderByStartAsc(anyLong(),anyLong(), any());
        verify(commentRepository).findByItemId(anyLong());
    }

    @Test
    void search() {

        assertIterableEquals(List.of(),itemService.search("",1,0,10));

        verify(itemRepository,never()).searchByIgnoreCaseDescriptionContainingAndAvailableTrue(anyString(),any());

        Pageable pageable = Util.createPageParam(0,10);

        when(itemRepository.searchByIgnoreCaseDescriptionContainingAndAvailableTrue("text",pageable)).thenReturn(itemList);

        List<ItemDto> itemDtos = itemService.search("text",1,0,10);

        assertIterableEquals(itemDtos,itemDtoList);

        verify(itemRepository).searchByIgnoreCaseDescriptionContainingAndAvailableTrue("text",pageable);

    }

    @Test
    void addComment() {

        assertThrows(BadRequestException.class,
                () -> itemService.addComment(new CreateCommentDto(1L,"text"),1,1),
                "У пользователя 1 небыло вещи 1 в аренде!");
        when(bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(),anyLong(),any(), any())).thenReturn(true);

        assertThrows(NotFoundException.class,
                () -> itemService.addComment(new CreateCommentDto(1L,"text"),1,1),
                "Не найдена вещь 1, при добовлении коментария");

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemList.get(0)));

        assertThrows(NotFoundException.class,
                () -> itemService.addComment(new CreateCommentDto(1L,"text"),1,1),
                "Не найден пользователь 1, при добовлении коментария");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userList.get(0)));

        Comment comment = new Comment(1,
                "комментарий 1",
                itemList.get(0),
                userList.get(0),
                LocalDateTime.of(2024,4,7,10,10));

        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto commentDto = itemService.addComment(new CreateCommentDto(1L,"text"),1,1);

        assertEquals(comment.getText(),commentDto.getText());
    }
}