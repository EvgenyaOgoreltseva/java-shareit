package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.create(userId, bookingRequestDto);
    }

    @PatchMapping(path = "/{id}")
    public BookingDto update(@PathVariable(value = "id") Long id,
                             @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                             @RequestParam(value = "approved") boolean approved) {
        return bookingService.update(id, userId, approved);
    }

    @GetMapping(path = "/{id}")
    public BookingDto findById(@PathVariable(value = "id") Long id,
                               @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.findById(id, userId);
    }

    @GetMapping
    public List<BookingDto> findByBooker(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @RequestParam(
                                                 value = "state",
                                                 required = false,
                                                 defaultValue = "ALL") BookingState state) {
        return bookingService.findByBooker(userId, state);
    }

    @GetMapping(path = "/owner")
    public List<BookingDto> findByOwner(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                        @RequestParam(
                                                value = "state",
                                                required = false,
                                                defaultValue = "ALL") BookingState state) {
        return bookingService.findByOwner(userId, state);
    }
}
