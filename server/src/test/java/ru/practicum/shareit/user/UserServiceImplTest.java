package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Util;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.data.Data.generationData;
import static ru.practicum.shareit.data.Data.printList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserMapper userMapper = new UserMapperImpl();

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private List<User> userList;

    private List<UserDto> userDtoList;

    @BeforeEach
    void start() {
        userMapper = new UserMapperImpl();
        userService = new UserServiceImpl(userMapper,userRepository);

        userList = generationData(2, User.class);
        printList(userList,">>>");

        userDtoList = userList.stream().map(user -> userMapper.toUserDto(user)).collect(Collectors.toList());
        printList(userDtoList,"<<<");

    }

    @Test
    void addUser() {
        when(userRepository.save(any())).thenReturn(userList.get(0));
        UserDto userDto = userService.addUser(userDtoList.get(0));
        assertNotNull(userDto);
        assertEquals(userDto,userDtoList.get(0));
        verify(userRepository).save(any());
    }

    @Test
    void upUser() {
        assertThrows(NotFoundException.class,() -> userService.upUser(userDtoList.get(0),1),
                "Не найден пользователь под id = 1");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userList.get(0)));
        when(userRepository.save(any())).thenReturn(userList.get(0));
        UserDto userDto = userService.upUser(userDtoList.get(0),1);
        assertEquals(userDto,userDtoList.get(0));
        verify(userRepository,times(2)).findById(anyLong());
        verify(userRepository).save(any());
    }

    @Test
    void deleteUser() {
        userService.deleteUser(1);
        verify(userRepository).deleteById(anyLong());
    }

    @Test
    void getUser() {
        assertThrows(NotFoundException.class,() -> userService.getUser(2),
                "Не найден пользователь под id = 2");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userList.get(0)));
        UserDto userDto = userService.getUser(1);
        assertNotNull(userDto);
        assertEquals(userDto,userDtoList.get(0));
        verify(userRepository,times(2)).findById(anyLong());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUsers() {
        Pageable pageable = Util.createPageParam(0,10, Sort.by(Sort.Direction.ASC,"id"));
        Page<User> userPage = new PageImpl<>(userList);
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        List<UserDto> userDtos = userService.getUsers(0,10);
        assertNotNull(userDtos);
        assertIterableEquals(userDtos,userDtoList);
        verify(userRepository).findAll(pageable);
    }
}