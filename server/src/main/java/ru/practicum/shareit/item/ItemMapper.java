package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "request", source = "itemDto.requestId")
    Item toItem(ItemDto itemDto);

    @Mapping(target = "requestId", source = "item.request")
    @Mapping(target = "owner", expression = "java(userToUserDto(item))")
    ItemDto toItemDto(Item item);

  /*  @Mapping(target = "requestId", source = "item.request")
    List<ItemDto> toItemDtoList(List<Item> items);*/

    default List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream().map(item -> toItemDto(item)).collect(Collectors.toList());
    }

    default UserDto userToUserDto(Item item) {
        return Mappers.getMapper(UserMapper.class).toUserDto(item.getOwner());
    }

}
