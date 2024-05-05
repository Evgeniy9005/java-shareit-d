package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest,Long> {

    List<ItemRequest> findByRequester(long userId);

    boolean existsByRequester(long userId);

}
