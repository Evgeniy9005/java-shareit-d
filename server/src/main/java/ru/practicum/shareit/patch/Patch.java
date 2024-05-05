package ru.practicum.shareit.patch;

import ru.practicum.shareit.item.booking.IndicatorBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

public class Patch {


    @Valid
    public static UserDto patchUserDto(UserDto updated, UserDto patch) {
        Long id = patch.getId();
        String name = patch.getName();
        String email = patch.getEmail();
        return updated.builder()
                .id(id == null ? updated.getId() : id)
                .name(name == null ? updated.getName() : name)
                .email(email == null ? updated.getEmail() : email)
                .build();
    }

    @Valid
    public static ItemDto patchItemDto(ItemDto updated, ItemDto patch) {
       Long id = patch.getId();
       String name = patch.getName();
       String description = patch.getDescription();
       Boolean available = patch.getAvailable();
       User owner = patch.getOwner();
       Long request = patch.getRequestId();
       IndicatorBooking lastBooking = patch.getLastBooking();
       IndicatorBooking nextBooking = patch.getNextBooking();
       return updated.builder()
               .id(id == null ? updated.getId() : id)
               .name(name == null ? updated.getName() : name)
               .description(description == null ? updated.getDescription() : description)
               .available(available == null ? updated.getAvailable() : available)
               .owner(owner == null ? updated.getOwner() : owner)
               .requestId(request == null ? updated.getRequestId() : request)
               .lastBooking(lastBooking == null ? updated.getLastBooking() : lastBooking)
               .nextBooking(nextBooking == null ? updated.getNextBooking() : nextBooking)
               .build();
    }
}
