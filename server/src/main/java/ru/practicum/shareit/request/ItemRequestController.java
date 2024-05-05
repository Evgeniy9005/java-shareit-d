package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestBody @Valid CreateItemRequest createRequest,
                                         @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return service.addItemRequest(createRequest, userId);
    }

    @GetMapping
    Collection<ItemRequestDto> getItemsRequester(@RequestHeader("X-Sharer-User-Id") Long userId) {

        return service.getItemsRequester(userId);
    }

    @GetMapping("/all")
    Collection<ItemRequestDto> getItemsRequesterPagination(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(defaultValue = "0") int from,
                                                           @RequestParam(defaultValue = "10") int size
    ) {
        return service.getItemsRequesterPagination(userId,from,size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestByIdForOtherUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PathVariable Long requestId
    ) {
        return service.getItemRequestByIdForOtherUser(userId,requestId);
    }
}
