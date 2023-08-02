package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.mapper.BookingMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    @Override
    @Transactional
    public BookingDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        User booker = getUserById(userId);
        Item item = getItemById(bookingRequestDto.getItemId());

        if (item.getOwner().getId().equals(userId)){
            throw new NotFoundException("Невозможно забронировать собственную вещь.");
        }
        if (!item.getAvailable()){
            throw new BadRequestException("Вещь уже забронирована!");
        }
        Booking booking = toBooking(bookingRequestDto,booker,item);
        log.info("Бронирование добавлено.");
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)){
            throw new NotFoundException("Невозможно изменить бронирование.");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BadRequestException("Невозможно изменить статус бронирования.");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Данные бронирования обновлены.");
        return toBookingDto(updatedBooking);
    }


    @Override
    @Transactional(readOnly = true)
    public BookingDto findById(Long bookingId, Long userId){
        Booking booking = getBookingById(bookingId);
        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Сведения о бронировании может получать только пользователь, " +
                    "оставивший бронь или владелец вещи!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findByBooker(Long userId, BookingState state) {
        getUserById(userId);
        List<Booking> bookings;
        switch (state){
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,BookingStatus.REJECTED);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
        return BookingMapper.getListOfBookingDto(bookings);
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findByOwner(Long userId, BookingState state) {
        getUserById(userId);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;

            default:
                throw new BadRequestException("Unknown state: " + state);
        }
        return getListOfBookingDto(bookings);
    }

    private User getUserById(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new NotFoundException("Неверный ID пользователя."));
    }

    private Item getItemById(Long itemId){
        return itemRepository.findById(itemId).orElseThrow(()->new NotFoundException("Неверный ID."));
    }

    private Booking getBookingById(Long bookingId){
        return bookingRepository.findById(bookingId).orElseThrow(()-> new NotFoundException("Бронирование на найдено!."));
    }

}

