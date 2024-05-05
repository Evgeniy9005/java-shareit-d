package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import java.util.List;


public interface ItemRepository extends JpaRepository<Item,Long> {

    List<Item> findByRequest(long requestId);

    @Query("select i from Item i where i.request in(:requestsId)")
    List<Item> findByRequest(@Param("requestsId") List<Long> requestsId);

    List<Item> findByOwnerId(Long userId, Pageable pageable);

    List<Item> searchByIgnoreCaseDescriptionContainingAndAvailableTrue(String text, Pageable pageable);


}
