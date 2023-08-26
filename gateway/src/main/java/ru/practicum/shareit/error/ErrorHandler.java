package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidExceptions(final MethodArgumentNotValidException e) {
        log.info("Получен статус 400 BadRequest {}", e.getMessage());
        return new ErrorResponse("Передан некорректный объект!", e.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.info("Получен статус 400 BadRequest {}", e.getMessage());
        return new ErrorResponse("Передан некорректный объект!", e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        log.info("Получен статус 400 BadRequest {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNotSpecializedExceptions(final Exception e) {
        log.info("Получен статус 500 InternalServerError {}", e.getMessage());
        return new ErrorResponse("Неизвестная ошибка!", e.getMessage());
    }
}