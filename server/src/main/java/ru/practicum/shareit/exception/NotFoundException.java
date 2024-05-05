package ru.practicum.shareit.exception;

public class NotFoundException extends FormatMassage {

    public NotFoundException(String massage) {
        super(massage);
    }

    public NotFoundException(String massage, Object param) {
        super(massage, param);
    }

    public NotFoundException(String massage, Object param1, Object param2) {
        super(massage, param1, param2);
    }

    public NotFoundException(String massage, Object param1, Object param2, Object param3) {
        super(massage, param1, param2, param3);
    }
}
