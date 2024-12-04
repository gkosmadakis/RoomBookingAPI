package com.acme.meetingroom.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acme.meetingroom.entity.Booking;
import com.acme.meetingroom.exception.BookingNotFoundException;
import com.acme.meetingroom.repository.BookingRepository;
import com.acme.meetingroom.utils.BookingValidator;

/**
 * Implementation of the {@link BookingService} interface. Handles the business
 * logic for managing meeting room bookings.
 */
@Service
public class BookingServiceImpl implements BookingService {
	private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

	private final BookingRepository bookingRepository;

	private final BookingValidator bookingValidator;

	/**
	 * Constructs a new instance of {@link BookingServiceImpl}.
	 *
	 * @param bookingRepository the repository used for managing booking data.
	 */
	public BookingServiceImpl(BookingRepository bookingRepository, BookingValidator bookingValidator) {
		this.bookingRepository = bookingRepository;
		this.bookingValidator = bookingValidator;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Booking> findBookingsByRoomAndDate(String room, LocalDate date) {
		return bookingRepository.findByRoomAndDate(room, date);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public Booking createBooking(String room, String employeeEmail, LocalDate date, LocalTime timeFrom,
			LocalTime timeTo) {
		// Validate the booking
		bookingValidator.validateBookingTimeFromTimeTo(room, date, timeFrom, timeTo);
		// Check for overlapping bookings
		if (bookingRepository.existsByRoomAndDateAndTimeFromLessThanEqualAndTimeToGreaterThanEqual(room, date, timeTo,
				timeFrom)) {
			logger.error("Room is already booked for this slot from time: {}, to time: {}", timeFrom, timeTo);
			throw new IllegalStateException("Room is already booked for this slot.");
		}
		// Create and save the new booking
		Booking booking = new Booking(room, employeeEmail, date, timeFrom, timeTo);
		return bookingRepository.save(booking);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws SQLException
	 */
	@Transactional
	public void cancelBooking(Long bookingId) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));
		// Validate that the booking is not in the past
		bookingValidator.validatePastBooking(booking);

		bookingRepository.deleteById(bookingId);
	}

}
