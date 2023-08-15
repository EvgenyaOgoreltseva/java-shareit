package ru.practicum.shareit.booking.dto;

import lombok.*;


@Data
@Builder
@AllArgsConstructor
public class BookingShortDto {
    private Long id;
    private Long bookerId;
}
