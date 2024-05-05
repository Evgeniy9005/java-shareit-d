package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto upUser(UserDto userDto, long userId);

    void deleteUser(long userId);

    UserDto getUser(long userId);

    List<UserDto> getUsers(int from, int size);

}
