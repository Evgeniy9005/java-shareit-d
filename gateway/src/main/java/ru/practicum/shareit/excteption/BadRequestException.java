package ru.practicum.shareit.excteption;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String massage) {
        super(massage);
    }

}
