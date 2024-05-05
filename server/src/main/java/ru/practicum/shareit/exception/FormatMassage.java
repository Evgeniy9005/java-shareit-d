package ru.practicum.shareit.exception;

public class FormatMassage extends RuntimeException {
    private static final String PARAM = "#";

    public FormatMassage(String massage) {
        super(massage);
    }

    public FormatMassage(String massage, Object param) {
        super(massage.replaceFirst(PARAM,param.toString()));
    }

    public FormatMassage(String massage, Object param1, Object param2) {
        super(massage.replaceFirst(PARAM,param1.toString())
                .replaceFirst(PARAM,param2.toString())
        );
    }

    public FormatMassage(String massage, Object param1, Object param2, Object param3) {
        super(massage.replaceFirst(PARAM,param1.toString())
                .replaceFirst(PARAM,param2.toString())
                .replaceFirst(PARAM,param3.toString())
        );
    }
}
