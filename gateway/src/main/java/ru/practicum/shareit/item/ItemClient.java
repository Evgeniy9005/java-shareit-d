package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Collection;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> addItem(ItemDto itemDto, Long userId) {

        return post("",userId,itemDto);
    }

    /*@PatchMapping(path = "/{itemId}")
    public ItemDto upItem(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.upItem(
                Patch.patchItemDto(itemService.getItem(itemId, userId), itemDto),
                itemId,
                userId
        );
    }*/
/*
    @GetMapping("/{itemId}")
    public ItemDto getItemByRequestUsers(@PathVariable Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userIdMakesRequest
    ) {
        return itemService.getItemByRequestUsers(itemId, userIdMakesRequest);
    }*/

/*
    @GetMapping
    public Collection<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size
    ) {
        return itemService.getItemsByUserId(userId, from, size);
    }
*/

/*    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text,
                                      @RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size
    ) {

        return itemService.search(text,userId,from,size);
    }*/

    /*@PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody @Valid CreateCommentDto createCommentDto,
                                 @PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long authorId) {

        return itemService.addComment(createCommentDto,itemId,authorId);
    }*/
}
