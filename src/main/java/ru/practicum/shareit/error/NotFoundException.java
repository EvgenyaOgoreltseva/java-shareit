package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        log.error(message);
    }
}
