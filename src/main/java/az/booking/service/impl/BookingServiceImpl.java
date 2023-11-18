package az.booking.service.impl;

import az.booking.domain.Booking;
import az.booking.domain.Payment;
import az.booking.domain.User;
import az.booking.dto.request.BookingRequest;
import az.booking.dto.response.BookingResponse;
import az.booking.errors.ApplicationException;
import az.booking.errors.Errors;
import az.booking.repository.BookingRepository;
import az.booking.repository.PaymentRepository;
import az.booking.repository.UserRepository;
import az.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    private final PaymentRepository paymentRepository;

    @Override
    public BookingResponse findById(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new ApplicationException(Errors.BOOKING_NOT_FOUND));
        return modelMapper.map(booking, BookingResponse.class);
    }

    @Override
    public List<BookingResponse> findAll() {
        return bookingRepository
                .findAll()
                .stream()
                .map(booking -> modelMapper.map(booking, BookingResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> save(Long userId, Long paymentId, BookingRequest bookingRequest) {
        User user = getUserById(userId);
        Payment payment = getPaymentById(paymentId);
        Booking booking = createBooking(user, payment, bookingRequest);
        return (List<BookingResponse>) mapToBookingResponse(booking);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ApplicationException(Errors.USER_NOT_FOUND));
    }

    private Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() ->
                new ApplicationException(Errors.PAYMENT_NOT_FOUND));
    }

    private Booking createBooking(User user, Payment payment, BookingRequest bookingRequest) {
        Booking booking = modelMapper.map(bookingRequest, Booking.class);
        booking.setPayment(payment);
        booking.setUser(user);
        return bookingRepository.save(booking);
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        return modelMapper.map(booking, BookingResponse.class);
    }

    @Override
    public void delete(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new ApplicationException(Errors.BOOKING_NOT_FOUND));
        bookingRepository.delete(booking);
    }
}