package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.util.Util;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestRepository repository;

    private final ItemRequestMapper mapper;

    private final ItemMapper itemMapper;

    @Transactional
    public ItemRequestDto addItemRequest(CreateItemRequest createRequest, long userId) {

        isUser("Не найден пользователь # при добавлении запроса на вещь!",userId);

        ItemRequest itemRequest = ItemRequest.builder()
                .description(createRequest.getDescription())
                .created(LocalDateTime.now())
                .requester(userId)
                .build();

        ItemRequest newItemRequest = repository.save(itemRequest);

        log.info("Добавлен запрос на вещь {}", newItemRequest);

        return mapper.toItemRequestDto(newItemRequest);
    }

    public List<ItemRequestDto> getItemsRequester(long userId) {

        isUser("Не найден пользователь # при запросе запрашиваемых вещей!",userId);

        List<ItemRequest> itemRequestList = repository.findByRequester(userId);

        List<Long> itemRequestsId = itemRequestList.stream().map(ItemRequest::getId).collect(Collectors.toList());

        List<Item> itemList = itemRepository.findByRequest(itemRequestsId);

        Map<Long,List<Item>> itemsOnRequestMap = itemList.stream().collect(Collectors.groupingBy(Item::getRequest));

        List<ItemRequestDto> itemRequestDtoList = mapper.toItemRequestDtoList(itemRequestList).stream()
                .map(itemRequestDto -> {
                    long idItemRequest = itemRequestDto.getId();
                    if (itemsOnRequestMap.containsKey(idItemRequest)) {
                        return itemRequestDto.toBuilder()
                                .items(itemMapper.toItemDtoList(itemsOnRequestMap.get(idItemRequest)))
                                .build();
                    }
                    return itemRequestDto;
                })
                .collect(Collectors.toList());

        return itemRequestDtoList;
    }

    public List<ItemRequestDto> getItemsRequesterPagination(long userId, int from, int size) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(
                    "Не найден пользователь # при запросе запрашиваемых вещей в диапазоне от # до #!",userId,from,size);
        }

        Pageable page = Util.createPageParam(from,size);

        if (repository.existsByRequester(userId)) {
            return new ArrayList<>();
        } else {
            List<ItemRequest> requests = Util.getElementsFrom(
                    repository.findAll(page).getContent(),Util.start(from,size)
            );

            log.info("Получены все ItemRequest в количестве {} в диапазоне от {} до {}",requests.size(),from,size);

            List<Long> itemRequestsId = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());

            List<Item> itemList = itemRepository.findByRequest(itemRequestsId);

            Map<Long,List<Item>> itemsOnRequestMap = itemList.stream().collect(Collectors.groupingBy(Item::getRequest));

            List<ItemRequestDto> itemRequestDtoList = mapper.toItemRequestDtoList(requests).stream()
                    .map(itemRequestDto -> {
                        long idItemRequest = itemRequestDto.getId();
                        if (itemsOnRequestMap.containsKey(idItemRequest)) {
                            return itemRequestDto.toBuilder()
                                    .items(itemMapper.toItemDtoList(itemsOnRequestMap.get(idItemRequest)))
                                    .build();
                        }
                        return itemRequestDto;
                    })
                    .collect(Collectors.toList());

            log.info("Получены все ItemRequestDto в количестве {} в диапазоне от {} до {}",
                    itemRequestDtoList.size(),
                    from,
                    size
            );

            return itemRequestDtoList;
        }

    }


    public ItemRequestDto getItemRequestByIdForOtherUser(long userId, long requestId) {
        isUser("Не найден пользователь # при запросе заказа на вещь " + requestId, userId);

        ItemRequest itemRequest = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Не найден запрос на вещь под id = " + requestId));

        ItemRequestDto itemRequestDto = mapper.toItemRequestDto(itemRequest).toBuilder()
                .items(itemMapper.toItemDtoList(itemRepository.findByRequest(requestId)))
                .build();

        log.info("Получен заказ на вещь {}",itemRequestDto);

        return itemRequestDto;
    }

    private void isUser(String textException, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(textException,userId);
        }
    }
}
