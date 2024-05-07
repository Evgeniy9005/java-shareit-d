package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.patch.Patch;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
    log.info("Добавлена вещь {}, пользователем {}!",itemDto,userId);
        return itemClient.addItem(itemDto,userId);
    }

    @PatchMapping(path = "/{itemId}")
    public ResponseEntity<Object> upItem(@RequestBody ItemDto itemDto,
                                         @Positive @PathVariable Long itemId,
                                         @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Обновление вещи {} , {}",itemId,itemDto);
        return itemClient.upItem(Patch.patchItemDto(itemDto),itemId,userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemByRequestUsers(@Positive @PathVariable Long itemId,
                                                        @Positive @RequestHeader("X-Sharer-User-Id") Long userIdMakesRequest
    ) {
        log.info("Вернуть вещь {} для пользователя {}",itemId,userIdMakesRequest);
        return itemClient.getItemByRequestUsers(itemId, userIdMakesRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("Вернуть вещи пользователя {} от {} до {}",userId,from,size);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("Найти вещи по тексту ({}) по запросу пользователя {} от {} до {}",text,userId,from,size);
        return itemClient.search(text,userId,from,size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Valid CreateCommentDto createCommentDto,
                                             @Positive @PathVariable Long itemId,
                                             @Positive @RequestHeader("X-Sharer-User-Id") Long authorId) {
        log.info("Добавить комментарий {} на вещь {} от пользователя {}",createCommentDto,itemId,authorId);
        return itemClient.addComment(createCommentDto,itemId,authorId);
    }
}
