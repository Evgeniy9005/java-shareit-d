package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestItemController {

    private final RequestItemClient requestItemClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestBody @Valid CreateItemRequest createRequest,
                                                 @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Создать заказ на вещь {} от пользователя {}",createRequest,userId);
        return requestItemClient.addItemRequest(createRequest, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsRequester(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вернуть все заказы на вещи от пользователя {}",userId);
        return requestItemClient.getItemsRequester(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemsRequesterPagination(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                              @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Вернуть все заказы на вещи от пользователя {} от {} до {}",userId,from,size);
        return requestItemClient.getItemsRequesterPagination(userId,from,size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestByIdForOtherUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                 @Positive @PathVariable Long requestId
    ) {
        log.info("Вернуть заказ {} на вещь для пользователя {}",userId,requestId);
        return requestItemClient.getItemRequestByIdForOtherUser(userId,requestId);
    }
}
