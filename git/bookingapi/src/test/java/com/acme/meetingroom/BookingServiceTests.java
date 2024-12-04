package com.acme.meetingroom;

import static org.assertj.core.api.Assertions.assertThat;
import com.acme.meetingroom.entity.Booking;
import com.acme.meetingroom.exception.BookingNotFoundException;
import com.acme.meetingroom.service.BookingServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingServiceTests {

	@Autowired
	private BookingServiceImpl bookingService;

	@Test
	void testCreateBooking() {
		Booking booking = bookingService.createBooking("Room1", "employee@example.com", LocalDate.now().plusDays(1),
				LocalTime.of(10, 0), LocalTime.of(12, 0));
		assertNotNull(booking);
	}

	@Test
	void testCreateInvalidTimeFromTimeToBooking() {
		assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking("Room1", "employee@example.com",
				LocalDate.now().plusDays(1), LocalTime.of(11, 0), LocalTime.of(10, 0)));
	}

	@Test
	void testCreateBookingDurationLessThanOneHour() {
		assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking("Room1", "employee@example.com",
				LocalDate.now().plusDays(1), LocalTime.of(10, 30), LocalTime.of(11, 0)));
	}

	@Test
	void testOverlappingBooking() {
		bookingService.createBooking("Room2", "employee1@example.com", LocalDate.now().plusDays(1), LocalTime.of(10, 0),
				LocalTime.of(12, 0));
		assertThrows(IllegalStateException.class, () -> bookingService.createBooking("Room2", "employee2@example.com",
				LocalDate.now().plusDays(1), LocalTime.of(11, 0), LocalTime.of(13, 0)));
	}

	@Test
	void testCancelExistentBooking() {
		Booking booking = bookingService.createBooking("Room1", "employee@example.com", LocalDate.now(),
				LocalTime.of(10, 0), LocalTime.of(12, 0));
		bookingService.cancelBooking(booking.getId());

		// Verify the booking is deleted
		List<Booking> remainingBookings = bookingService.findBookingsByRoomAndDate("Room1", LocalDate.now());
		assertThat(remainingBookings).isEmpty();
	}

	@Test
	void testCancelNonExistentBooking() {
		Booking booking = bookingService.createBooking("Room1", "employee@example.com", LocalDate.now().plusDays(1),
				LocalTime.of(10, 0), LocalTime.of(12, 0));
		bookingService.cancelBooking(booking.getId());
		assertThrows(BookingNotFoundException.class, () -> bookingService.cancelBooking(booking.getId()));
	}

	@Test
	void testCancelPastBooking() {
		Booking booking = bookingService.createBooking("Room1", "employee@example.com", LocalDate.now().minusDays(1),
				LocalTime.of(10, 0), LocalTime.of(12, 0));
		assertThrows(IllegalStateException.class, () -> bookingService.cancelBooking(booking.getId()));
	}
}
