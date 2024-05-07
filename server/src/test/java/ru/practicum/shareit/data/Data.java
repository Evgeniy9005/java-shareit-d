package ru.practicum.shareit.data;


import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
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
     <p><b>- User</b> баз параметров objects;</p>
     <p><b>- ItemRequest</b> баз параметров objects;</p>
     <p><b>- Item</b> параметр 1 objects[0] User, параметр 2 objects[1] Long идентификатор requester;</p>
     <p><b>- Booking</b> параметр 1 objects[0] User, параметр 2 objects[1] Item;</p>
     <p><b>- CreateBooking</b> параметр 1 objects[0] Long идентификатор Item;</p>
     <p><b>- Comment</b>  параметр 1 objects[0] Item, параметр 2 objects[1] User.</p>
     */

    public static <T> List<T> generationData(Integer createObjects, Type t, Object... objects) {

        return (List<T>) IntStream.iterate(1,i -> i + 1)
                        .mapToObj(i -> getData(i, t, objects))
                        .limit(createObjects)
                        .collect(Collectors.toList());
    }

    private static <D> D getData(int i, Type type, Object... objects) {

        if (type.equals(User.class)) {
            return (D) new User(i,"User" + i,"user" + i + "@mail");
        }

        if (type.equals(ItemRequest.class)) {
            return (D) ItemRequest.builder()
                    .id(i)
                    .requester(1L)
                    .description("Запрос на вещь " + i)
                    .created(LocalDateTime.now())
                    .build();
        }

        if (type.equals(Item.class)) {
            if (objects.length == 2) {
                if (objects[0].getClass().equals(User.class) && objects[1].getClass().equals(Long.class)) {

                    return (D) new Item(i, "item" + i, "описание вещи " + i, true, (User) objects[0],(long)objects[1]);
                }
            }
        }


        if (type.equals(Booking.class)) {
            if (objects.length == 2) {
                if (objects[0].getClass().equals(User.class) && objects[1].getClass().equals(Item.class)) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                    return (D) Booking.builder()
                            .id(i)
                            .booker((User) objects[0])
                            .item((Item) objects[1])
                            .start(LocalDateTime.now())
                            .end(LocalDateTime.now().plusDays(1))
                            .status(Status.APPROVED)
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

        if (type.equals(Comment.class)) {
            if (objects.length == 2) {
                if (objects[0].getClass().equals(Item.class) && objects[1].getClass().equals(User.class)) {
                    return (D) Comment.builder()
                            .id(i)
                            .text("Text" + i)
                            .item((Item) objects[0])
                            .author((User) objects[1])
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
