package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.patch.Patch;
import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {

        return itemService.addItem(itemDto,userId);
    }

    @PatchMapping(path = "/{itemId}")
    public ItemDto upItem(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
       return itemService.upItem(
                Patch.patchItemDto(itemService.getItem(itemId, userId), itemDto),
                itemId,
                userId
        );
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemByRequestUsers(@PathVariable Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userIdMakesRequest
    ) {
        return itemService.getItemByRequestUsers(itemId, userIdMakesRequest);
    }

    @GetMapping
    public Collection<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size
    ) {
        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text,
                                      @RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size
    ) {

        return itemService.search(text,userId,from,size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CreateCommentDto createCommentDto,
                                 @PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long authorId) {

        return itemService.addComment(createCommentDto,itemId,authorId);
    }


}
