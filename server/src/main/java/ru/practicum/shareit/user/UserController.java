package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto upUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        return userService.upUser(userDto,userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public Collection<UserDto> getUsers(@RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getUsers(from,size);
    }

}
