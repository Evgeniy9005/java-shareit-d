package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.util.Util;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.data.Data.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private ItemRequestService itemRequestService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();

    private List<Item> itemList;

    private List<User> userList;

    private List<ItemRequest> itemRequestList;


    @BeforeEach
    void start() {
    itemRequestService = new ItemRequestServiceImpl(userRepository,
            itemRepository,itemRequestRepository,new ItemRequestMapperImpl(),new ItemMapperImpl());

        userList = generationData(2,User.class);
        printList(userList,">>>");

        itemList = generationData(2, Item.class, userList.get(0),1L);
        printList(itemList,"===");

        itemRequestList = generationData(3,ItemRequest.class);
        printList(itemRequestList,"---");

    }

    @Test
    void  returnNullMapper() {
        List<ItemRequest> itemRequests = null;
        assertNull(itemRequestMapper.toItemRequestDtoList(itemRequests));
    }

    @Test
    void addItemRequest() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.addItemRequest(any(),1L),
                "Не найден пользователь 1 при добавлении запроса на вещь!");

        when(userRepository.existsById(anyLong())).thenReturn(true);

        itemRequestService.addItemRequest(new CreateItemRequest(0,"Дрель"),1L);

        verify(itemRequestRepository).save(any(ItemRequest.class));

    }


    @Test
    void getItemsRequester() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemsRequester(1L),
                "Не найден пользователь 1 при запросе запращиваемых вещей!");

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(itemRequestRepository.findByRequester(1L)).thenReturn(itemRequestList);

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getItemsRequester(1L);

        assertIterableEquals(itemRequestMapper.toItemRequestDtoList(itemRequestList),itemRequestDtoList);

    }

    @Test
    void getItemsRequesterPagination() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemsRequesterPagination(1L,0,10),
                "Не найден пользователь 1 при запросе запрашиваемых вещей в диапазоне от 0 до 10!");

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(itemRequestRepository.existsByRequester(anyLong())).thenReturn(true);

        List<ItemRequestDto> itemRequestDtoList = itemRequestService
                .getItemsRequesterPagination(1L,0,10);

        assertIterableEquals(List.of(),itemRequestDtoList);

        when(itemRequestRepository.existsByRequester(anyLong())).thenReturn(false);
        Pageable pageable = Util.createPageParam(0,10);
        Page<ItemRequest> itemRequestPage = new PageImpl<>(itemRequestList);
        when(itemRequestRepository.findAll(pageable)).thenReturn(itemRequestPage);

        itemRequestDtoList = itemRequestService.getItemsRequesterPagination(1L,0,10);

        assertIterableEquals(itemRequestMapper.toItemRequestDtoList(itemRequestList),itemRequestDtoList);

        verify(itemRequestRepository,times(2)).existsByRequester(1);
        verify(itemRequestRepository).findAll(pageable);
        verify(itemRepository).findByRequest(List.of(1L,2L,3L));
        verify(itemRepository).findByRequest(any());
    }

    @Test
    void getItemRequestByIdForOtherUser() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestByIdForOtherUser(1,1),
                "Не найден пользователь 1 при запросе заказа на вещь 1");

        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestByIdForOtherUser(1,1),
                "Не найден запрос на вещь под id = 1");

        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequestList.get(0)));
        when(itemRepository.findByRequest(1)).thenReturn(itemList);
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestByIdForOtherUser(1,1);
        assertEquals(2,itemRequestDto.getItems().size());

        verify(itemRequestRepository,times(2)).findById(any());
        verify(itemRepository).findByRequest(anyLong());
        verify(userRepository,times(3)).existsById(anyLong());

    }
}