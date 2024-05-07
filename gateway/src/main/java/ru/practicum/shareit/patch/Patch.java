package ru.practicum.shareit.patch;

import ru.practicum.shareit.item.dto.IndicatorBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

public class Patch {

    public static UserDto patchUserDto(UserDto patch) {
        UserDto updated = UserDto.builder().email("putch@putch.putch").build();
        String name = patch.getName();
        String email = patch.getEmail();
        @Valid UserDto userDto = updated.builder()
                .name(name == null ? updated.getName() : name)
                .email(email == null ? updated.getEmail() : email)
                .build();

        return userDto.toBuilder()
                .email(email == "putch@putch.putch" ? updated.getEmail() : email)
                .build();
    }


    public static ItemDto patchItemDto(ItemDto patch) {
        String availablePatch = "";
        if (patch.getAvailable() == null) {
            availablePatch = "patch";
        }
       ItemDto updated =  ItemDto.builder().name("patch").description("patch").available(false).build();
       String name = patch.getName();
       String description = patch.getDescription();
       Boolean available = patch.getAvailable();
       UserDto owner = patch.getOwner();
       Long request = patch.getRequestId();
       IndicatorBooking lastBooking = patch.getLastBooking();
       IndicatorBooking nextBooking = patch.getNextBooking();
       @Valid ItemDto itemDto = updated.builder()
                .name(name == null ? updated.getName() : name)
                .description(description == null ? updated.getDescription() : description)
                .available(available == null ? updated.getAvailable() : available)
                .owner(owner == null ? updated.getOwner() : owner)
                .requestId(request == null ? updated.getRequestId() : request)
                .lastBooking(lastBooking == null ? updated.getLastBooking() : lastBooking)
                .nextBooking(nextBooking == null ? updated.getNextBooking() : nextBooking)
                .build();

       return itemDto.toBuilder()
                .name(name == "patch" ? updated.getName() : name)
                .description(description == "patch" ? updated.getDescription() : description)
                .available(availablePatch == "patch" ? null : available)
                .build();
    }
}
