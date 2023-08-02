package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingRequestDto bookingRequestDto, Long userId);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long bookingId, Long userId);

    List<BookingDto> findByBooker(Long userId, BookingState state);

    List<BookingDto> findByOwner(Long userId, BookingState state);
}
