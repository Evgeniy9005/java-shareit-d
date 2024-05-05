package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequestList);

}
