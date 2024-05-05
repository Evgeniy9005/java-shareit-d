package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final NotFoundException e) {
        log.debug("Получен статус 404 Not found {}",e.getMessage(),e);
        return new ErrorResponse(
                e.getClass().getName(), e.getMessage()
        );
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final BadRequestException e) {
        log.debug("Получен статус 400 Bad request {}",e.getMessage(),e);
        return new ErrorResponse(
                e.getClass().getName(), e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final UnsupportedStatusException e) {
        log.debug("Получен статус 400 Bad request {}",e.getMessage(),e);
        return new ErrorResponse(
                e.getMessage(), e.getClass().getName()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final RuntimeException e) {
        e.printStackTrace();
        log.debug("Получен статус 500 internal server error {}",e.getMessage(),e);
        return new ErrorResponse(
                e.toString(), "Произошла непредвиденная ошибка!"
        );
    }
}
