package ru.practicum.shareit.request.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;


import javax.persistence.EntityManager;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private List<User> savedUser;

    private List<ItemRequest> savedItemRequests;


    private void createItemRequest(int quantity, Type type) {
        Data.<ItemRequest>generationData(quantity,type)
                .stream()
                .map(itemRequest -> itemRequestRepository.save(itemRequest))
                .collect(Collectors.toList());
    }

    @BeforeEach
    void start() {

        savedUser = Data.<User>generationData(2,User.class)
                .stream()
                .map(user -> userRepository.save(user))
                .collect(Collectors.toList());
        Data.printList(savedUser,"-&-");

        savedItemRequests = Data.<ItemRequest>generationData(3,ItemRequest.class)
                .stream()
                .map(itemRequest -> itemRequestRepository.save(itemRequest))
                .collect(Collectors.toList());
        Data.printList(savedItemRequests,"*^*");
    }

    @AfterEach
    void end() {
        entityManager.createNativeQuery("ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE ITEM_REQUESTS ALTER COLUMN ID RESTART WITH 1").executeUpdate();
    }


    @Test
    void findByRequester() {

        assertEquals(3,itemRequestRepository.findByRequester(1).size(),
                "у пользователя 1 3 запроса");

        assertEquals(0,itemRequestRepository.findByRequester(2).size(),
                "у пользователя 2 не запросов");

    }

    @Test
    void findByItemRequestPagination() {

        createItemRequest(11,ItemRequest.class);

        Sort sortByDate = Sort.by(Sort.Direction.DESC,"created");
        Pageable page = PageRequest.of(0,5,sortByDate);

            Page<ItemRequest> itemRequestPage1 = itemRequestRepository.findAll(page);

            assertEquals(3,itemRequestPage1.getTotalPages());

            List<ItemRequest> list1 = itemRequestPage1.getContent();

            assertEquals(5,list1.size());

        page = PageRequest.of(itemRequestPage1.getNumber() + 1,
                itemRequestPage1.getSize(),
                itemRequestPage1.getSort());

        Page<ItemRequest> itemRequestPage2 = itemRequestRepository.findAll(page);

        assertEquals(3,itemRequestPage2.getTotalPages());

        List<ItemRequest> list2 = itemRequestPage2.getContent();

        assertEquals(5,list2.size());

        page = itemRequestPage2.nextOrLastPageable();

        Page<ItemRequest> itemRequestPage3 = itemRequestRepository.findAll(page);

        assertEquals(3,itemRequestPage3.getTotalPages());

        List<ItemRequest> list3 = itemRequestPage3.getContent();

        assertEquals(1,list3.size());

        page = itemRequestPage2.nextOrLastPageable();

        page = PageRequest.of(0,5,sortByDate);
        Page<ItemRequest> itemRequestPage4 = itemRequestRepository.findAll(page);
        Data.printList(itemRequestPage4.getContent(),"===");

        page = PageRequest.of(0,3);
        Page<ItemRequest> itemRequestPage5 = itemRequestRepository.findAll(page);
        Data.printList(itemRequestPage5.getContent(),"^^^");

        page = PageRequest.of(1,3);
        Page<ItemRequest> itemRequestPage6 = itemRequestRepository.findAll(page);
        Data.printList(itemRequestPage6.getContent(),">>>");

        page = PageRequest.of(1,3);
        Page<ItemRequest> itemRequestPage7 = itemRequestRepository.findAll(page);
        Data.printList(itemRequestPage7.getContent(),":::");

        page = PageRequest.of(2,3);
        Page<ItemRequest> itemRequestPage8 = itemRequestRepository.findAll(page);
        Data.printList(itemRequestPage8.getContent(),"*T*");

        page = PageRequest.of(3,3);
        Page<ItemRequest> itemRequestPage9 = itemRequestRepository.findAll(page);
        Data.printList(itemRequestPage9.getContent(),"|||");

    }


    @Test
    void existByRequester() {
        List<ItemRequest> savedItemRequester = Data.<ItemRequest>generationData(1,ItemRequest.class)
                .stream()
                .map(itemRequest -> itemRequestRepository.save(itemRequest))
                .collect(Collectors.toList());

        assertEquals(1,savedItemRequester.size());
        assertEquals(1,savedItemRequester.get(0).getRequester());

        assertTrue(itemRequestRepository.existsByRequester(savedUser.get(0).getId()));

    }

}