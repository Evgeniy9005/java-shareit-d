package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;


import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.data.Data.generationData;
import static ru.practicum.shareit.data.Data.printList;



@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private List<User> userList;

    private List<UserDto> userDtoList;

    private UserMapper userMapper = new UserMapperImpl();

    @BeforeEach
    void start() {
        userList = generationData(2, User.class);
        printList(userList,">>>");

        userDtoList = userList.stream().map(user -> userMapper.toUserDto(user)).collect(Collectors.toList());
        printList(userDtoList,"<<<");
    }

    @Test
    void addUser() throws Exception {

        UserDto userDto = userDtoList.get(0);

        mvc.perform(post("/user")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        when(userService.addUser(any(UserDto.class))).thenReturn(userDto);

        mvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id",is(1),Integer.class))
        .andExpect(jsonPath("$.name",is(userDto.getName())))
        .andExpect(jsonPath("$.email",is(userDto.getEmail())));

    }

    @Test
    void upUser() throws Exception {
        UserDto userDto = userDtoList.get(0);

        when(userService.upUser(any(UserDto.class),anyLong())).thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(1),Integer.class))
                .andExpect(jsonPath("$.name",is(userDto.getName())))
                .andExpect(jsonPath("$.email",is(userDto.getEmail())));

    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser(isA(Long.class));
        mvc.perform(delete("/users/1")).andExpect(status().isOk());
        verify(userService).deleteUser(1);

    }

    @Test
    void getUser() throws Exception {
        UserDto userDto = userDtoList.get(0);
        when(userService.getUser(1)).thenReturn(userDto);
        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(1),Integer.class))
                .andExpect(jsonPath("$.name",is(userDto.getName())))
                .andExpect(jsonPath("$.email",is(userDto.getEmail())));

    }

    @Test
    void getUsers() throws Exception {
        UserDto userDto1 = userDtoList.get(0);
        UserDto userDto2 = userDtoList.get(1);

        when(userService.getUsers(isA(Integer.class),isA(Integer.class))).thenReturn(userDtoList);
        mvc.perform(get("/users")
                        .content(objectMapper.writeValueAsString(userDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name",is(userDto1.getName())))
                .andExpect(jsonPath("$[0].email",is(userDto1.getEmail())))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name",is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email",is(userDto2.getEmail())));
    }

}