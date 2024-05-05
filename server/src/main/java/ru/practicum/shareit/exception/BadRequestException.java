package ru.practicum.shareit.exception;

public class BadRequestException extends FormatMassage {
    public BadRequestException(String massage) {
        super(massage);
    }

    public BadRequestException(String massage, Object param) {
        super(massage, param);
    }

    public BadRequestException(String massage, Object param1, Object param2) {
        super(massage, param1, param2);
    }

    public BadRequestException(String massage, Object param1, Object param2, Object param3) {
        super(massage, param1, param2, param3);
    }
}
