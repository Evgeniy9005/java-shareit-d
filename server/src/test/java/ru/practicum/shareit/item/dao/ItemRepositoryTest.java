package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager entityManager;

    List<User> savedUser;

    List<ItemRequest> savedItemRequest;

    List<Item> savedItems;

    List<Comment> savedComments;


    @BeforeEach
    void start() {
        savedUser = Data.<User>generationData(2,User.class)
                .stream()
                .map(user -> userRepository.save(user))
                .collect(Collectors.toList());
        Data.printList(savedUser,"*$*");

        savedItemRequest = Data.<ItemRequest>generationData(2,ItemRequest.class)
                .stream()
                .map(itemRequest -> itemRequestRepository.save(itemRequest))
                .collect(Collectors.toList());
        Data.printList(savedItemRequest,">>>");

        savedItems = Data.<Item>generationData(5,Item.class,savedUser.get(0),1L)
                .stream()
                .map(item -> itemRepository.save(item))
                .collect(Collectors.toList());
        Data.printList(savedItems,"^^^");

        Data.<Item>generationData(3,Item.class,savedUser.get(1),1L)
                .stream()
                .map(item -> savedItems.add(itemRepository.save(item.toBuilder().id(0).request(2L).build())))
                .collect(Collectors.toList());
        Data.printList(savedItems,"_=_");

        savedComments = Data.<Comment>generationData(3,Comment.class,savedItems.get(0),savedUser.get(1))
                .stream()
                .map(comment -> commentRepository.save(comment))
                .collect(Collectors.toList());
        Data.printList(savedComments,"ccc");

    }

    @AfterEach
    void end() {
        entityManager.createNativeQuery("ALTER TABLE ITEMS ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE ITEM_REQUESTS ALTER COLUMN ID RESTART WITH 1").executeUpdate();
    }

    @Test
    void findByOwnerId() {
        Pageable pageable = PageRequest.of(0,10);
        List<Item> items = itemRepository.findByOwnerId(1L,pageable);
        assertEquals(5,items.size());
    }

    @Test
    void searchByIgnoreCaseDescriptionContainingAndAvailableTrue() {
        Pageable pageable = PageRequest.of(0,10);
        List<Item> items = itemRepository.searchByIgnoreCaseDescriptionContainingAndAvailableTrue("вещ",pageable);
        assertEquals(8,items.size());
    }

    @Test
    void findByRequest() {
      List<Item> itemList = itemRepository.findByRequest(1L);
      assertEquals(5,itemList.size());
      assertEquals(3,itemRepository.findByRequest(2L).size());
      assertEquals(8,itemRepository.findByRequest(List.of(1L,2L)).size());
      assertEquals(5,itemRepository.findByRequest(List.of(1L)).size());
      assertEquals(3,itemRepository.findByRequest(List.of(2L)).size());
    }

    @Test
    void findByItemId() {
       List<Comment> commentList = commentRepository.findByItemId(1);
       assertEquals(3,commentList.size());
    }

}