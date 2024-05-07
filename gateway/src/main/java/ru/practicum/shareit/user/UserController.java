package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.patch.Patch;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDto userDto) {

        ResponseEntity<Object> user = userClient.addUser(userDto);
        log.info("Создать пользователя {}", userDto);
        return user;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> upUser(@RequestBody UserDto userDto,
                                         @Positive @PathVariable Long userId
    ) {
        log.info("Обновление пользователя {}",userId);
        return userClient.upUser(Patch.patchUserDto(userDto),userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@Positive @PathVariable Long userId) {
       log.info("Удаление пользователя");
       return userClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@Positive @PathVariable Long userId) {
        log.info("Возврат пользователя {}",userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Вернуть пользователей от {} до {}",from,size);
        return userClient.getUsers(from,size);
    }
}
