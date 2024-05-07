package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

public class DifferentTests {

    @Test
    void test1() {
    Object o = new User();
        System.out.println(o.getClass().getName());
        System.out.println(o.getClass());
        System.out.println(User.class.getName());
        System.out.println(o.getClass().equals(User.class));
    }

    @Test
    void test2() {
        Object o = 1L;
        System.out.println(1L == (long) o);
        System.out.println(o.getClass().equals(Long.class));
    }

    @Test
    void test3() {
        System.out.println(5 / 2);
        System.out.println(8 % 3);
        System.out.println(1 / 10);
        System.out.println(9 % 10);
    }

}
