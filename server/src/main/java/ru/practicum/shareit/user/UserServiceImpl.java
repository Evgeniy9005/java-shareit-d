package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.patch.Patch;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Util;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {

        User user = userRepository.save(userMapper.toUser(userDto));

        UserDto addUserDto = userMapper.toUserDto(user);

        log.info("Добавлен пользователь {}", addUserDto);

        return addUserDto;
    }

    @Override
    @Transactional
    public UserDto upUser(UserDto userDto, long userId) {

        User upUser = userMapper.toUser(
                Patch.patchUserDto(this.getUser(userId),userDto)
        );

        upUser.setId(userId);

        UserDto upUserDto = userMapper.toUserDto(userRepository.save(upUser));

        log.info("Обновлен пользователь {}", upUserDto);

        return upUserDto;
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {

        userRepository.deleteById(userId);
        log.info("Удален пользователь {}", userId);

    }

    @Override
    public UserDto getUser(long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Не найден пользователь под id = " + userId)
        );

        log.info("Возвращается пользователь {}", user);

        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers(int from, int size) {

        Sort sortById = Sort.by(Sort.Direction.ASC,"id");

        Pageable page = Util.validPageParam(from,size,sortById);

        List<UserDto> set = Util.getElementsFrom(userRepository.findAll(page).stream()
                .map(user -> userMapper.toUserDto(user))
                .collect(Collectors.toList()),Util.start(from,size));

        log.info("Возвращены все пользователи в количестве " + set.size());

        return set;
    }

}
