package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(CreateItemRequest createRequest, long userId);

    List<ItemRequestDto> getItemsRequester(long userId);

    List<ItemRequestDto> getItemsRequesterPagination(long userId, int from, int size);

    ItemRequestDto getItemRequestByIdForOtherUser(long userId, long requestId);
}
