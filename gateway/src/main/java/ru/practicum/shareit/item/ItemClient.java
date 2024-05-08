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
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.Map;

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

    public ResponseEntity<Object> upItem(ItemDto itemDto, Long itemId, Long userId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return patch("/{itemId}",userId,parameters,itemDto);
    }

    public ResponseEntity<Object> getItemByRequestUsers(Long itemId, Long userIdMakesRequest) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return get("/{itemId}",userIdMakesRequest,parameters);
    }


    public ResponseEntity<Object> getItemsByUserId(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/?from={from}&size={size}",userId,parameters);
    }

    public ResponseEntity<Object> search(String text, Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?&text={text}&from={from}&size={size}",userId,parameters);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(CreateCommentDto createCommentDto, Long itemId, Long authorId) {

        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return post("/{itemId}/comment",authorId,parameters,createCommentDto);
    }
}
