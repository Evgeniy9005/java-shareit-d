package ru.practicum.shareit.data;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Data {

    public static final String ALL = "ALL";
    public static final String FUTURE = "FUTURE";
    public static final String WAITING = "WAITING";
    public static final String REJECTED = "REJECTED";
    public static final String CURRENT = "CURRENT";
    public static final String PAST = "PAST";
    public static final String UNSUPPORTED_STATUS = "UNSUPPORTED_STATUS";
    public static final String DEFAULT = "DEFAULT";
    public static final LocalDateTime CREATED_DATE = LocalDateTime.of(2024,1,1,1,1);
    public static final LocalDateTime START_DATE = LocalDateTime.of(2024,2,2,2,2);
    public static final LocalDateTime END_DATE = LocalDateTime.of(2024,3,3,3,3);
    /**
     <p><b>- UserDto</b> баз параметров objects;</p>
     <p><b>- ItemRequestDto</b> баз параметров objects;</p>
     <p><b>- ItemDto</b> параметр 1 objects[0] User, параметр 2 objects[1] Long идентификатор requester;</p>
     <p><b>- BookingDto</b> параметр 1 objects[0] User, параметр 2 objects[1] Item;</p>
     <p><b>- CreateBooking</b> параметр 1 objects[0] Long идентификатор Item;</p>
     <p><b>- CommentDto</b> параметр 1 objects[1] UserDto.</p>
     */

    public static <T> List<T> generationData(Integer createObjects, Type t, Object... objects) {

        return (List<T>) IntStream.iterate(1,i -> i + 1)
                        .mapToObj(i -> getData(i, t, objects))
                        .limit(createObjects)
                        .collect(Collectors.toList());
    }

    private static <D> D getData(long i, Type type, Object... objects) {

        if (type.equals(UserDto.class)) {
            return (D) UserDto.builder().id(i).name("User" + i).email("user" + i + "@mail").build();
        }

        if (type.equals(ItemRequestDto.class)) {
            return (D) ItemRequestDto.builder()
                    .id(i)
                    .requester(1L)
                    .description("Запрос на вещь " + i)
                    .created(LocalDateTime.now())
                    .build();
        }

        if (type.equals(ItemDto.class)) {
            if (objects.length == 2) {
                if (objects[0].getClass().equals(UserDto.class) && objects[1].getClass().equals(Long.class)) {
                    return (D) ItemDto.builder()
                            .id(i)
                            .name("item" + i)
                            .description("описание вещи " + i)
                            .available(true)
                            .owner((UserDto) objects[0])
                            .requestId((long)objects[1])
                            .build();
                }
            }
        }

        if (type.equals(BookingDto.class)) {
            if (objects.length == 2) {
                if (objects[0].getClass().equals(UserDto.class) && objects[1].getClass().equals(ItemDto.class)) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                    return (D) BookingDto.builder()
                            .id(i)
                            .booker((UserDto) objects[0])
                            .item((ItemDto) objects[1])
                            .start(LocalDateTime.now())
                            .end(LocalDateTime.now().plusDays(1))
                            .status("APPROVED")
                            .build();
                }
            }
        }

        if (type.equals(CreateBooking.class)) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            if (objects.length == 1) {
                if (objects[0].getClass().equals(Long.class)) {
                    return (D) new CreateBooking((long) objects[0],
                            LocalDateTime.now().plusSeconds(60),
                            LocalDateTime.now().plusDays(1));
                }
            }
        }

        if (type.equals(CommentDto.class)) {
            if (objects.length == 1) {
                if (objects[0].getClass().equals(UserDto.class)) {
                    UserDto userDto = (UserDto) objects[0];
                    return (D) CommentDto.builder()
                            .id(i)
                            .text("Text" + i)
                            .authorName(userDto.getName())
                            .created(LocalDateTime.of(2024,1,1,1,1,1))
                            .build();
                }
            }
        }

        return null;
    }

    public static <T> void printList(Collection<T> list, String separator) {
        System.out.println(separator.repeat(33));
        list.stream().forEach(System.out::println);
        System.out.println(separator.repeat(33));
    }

    public static <T> void printList(Collection<T> list) {
        System.out.println("~*~".repeat(33));
        list.stream().forEach(System.out::println);
        System.out.println("~*~".repeat(33));
    }

    public static void separato__________________________________r() {
        System.out.println("***************************************************************");
    }

}
