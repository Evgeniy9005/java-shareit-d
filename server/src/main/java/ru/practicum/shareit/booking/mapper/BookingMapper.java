package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "item", expression = "java(itemToItemDto(booking))")
    @Mapping(target = "booker", expression = "java(userToUserDto(booking))")
    BookingDto toBookingDto(Booking booking);

    List<BookingDto> toBookingDtoList(List<Booking> bookings);

    default ItemDto itemToItemDto(Booking booking) {
        return Mappers.getMapper(ItemMapper.class).toItemDto(booking.getItem());
    }

    default UserDto userToUserDto(Booking booking) {
        return Mappers.getMapper(UserMapper.class).toUserDto(booking.getBooker());
    }

}
