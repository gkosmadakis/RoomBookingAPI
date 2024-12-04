package com.acme.meetingroom.utils;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.acme.meetingroom.entity.Booking;

@Component
public class BookingValidator {
	private static final Logger logger = LoggerFactory.getLogger(BookingValidator.class);

	public void validateBookingTimeFromTimeTo(String room, LocalDate date, LocalTime timeFrom, LocalTime timeTo) {
		if (timeTo.isBefore(timeFrom)) {
			logger.error("Invalid booking duration for room: {}, date: {}.Start time is greater than end time.", room,
					date);
			throw new IllegalArgumentException("Invalid booking duration. Start time is greater than end time.");
		}
		if (timeFrom.until(timeTo, MINUTES) < 60) {
			logger.error("Invalid booking duration for room: {}, date: {}.Must be at least 1 hour.", room, date);
			throw new IllegalArgumentException("Invalid booking duration. Must be at least 1 hour.");
		}
	}

	public Map<String, String> validateBooking(Booking booking) {
		Map<String, String> errors = new HashMap<>();
		if (StringUtils.isEmpty(booking.getEmployeeEmail())) {
			errors.put("Employee email validation error", "Employee email is empty");
		}
		if (booking.getDate() == null) {
			errors.put("Date validation error", "Date is empty");
		}
		if (booking.getTimeFrom() == null) {
			errors.put("Time from validation error", "Time from is empty");
		}
		if (booking.getTimeTo() == null) {
			errors.put("Time to validation error", "Time to is empty");
		} else if (booking.getDate() != null && booking.getDate().isBefore(LocalDate.now())) {
			errors.put("Date value invalid", "Date is in the past");
		}
		return errors;
	}

	public void validatePastBooking(Booking booking) {
		if (booking.getDate().isBefore(LocalDate.now())) {
			throw new IllegalStateException("Cannot cancel past bookings");
		}
	}

	public Map<String, String> validateRoomAndDate(String room, LocalDate date) {
		Map<String, String> errors = new HashMap<>();
		// Validate room parameter
		if (room == null || room.trim().isEmpty()) {
			logger.error("Invalid room: {} supplied. Room is empty.", room);
			errors.put("Room validation error", "Room parameter is empty");
			return errors;
		}
		// Validate date parameter
		if (date.isBefore(LocalDate.now())) {
			logger.error("Invalid date: {},supplied. Date supplied is in the past.", date);
			errors.put("Date validation error", "Date parameter is in the past");
			return errors;
		}
		return null;
	}
}
