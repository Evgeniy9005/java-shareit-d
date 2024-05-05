package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto upItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getItem(long itemId, long userId);

    List<ItemDto> getItemsByUserId(long userId, int from, int size);

    ItemDto getItemByRequestUsers(long itemId, long userIdMakesRequest);

    List<ItemDto> search(String text, long userId, int from, int size);

    CommentDto addComment(CreateCommentDto createCommentDto, long itemId, long authorId);
}
