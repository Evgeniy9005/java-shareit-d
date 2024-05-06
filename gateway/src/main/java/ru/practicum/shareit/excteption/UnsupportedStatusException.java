package ru.practicum.shareit.excteption;

public class UnsupportedStatusException extends RuntimeException {

    public UnsupportedStatusException(String massage) {
        super(massage);
    }

}
