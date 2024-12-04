package com.acme.meetingroom.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.acme.meetingroom.entity.Booking;

/**
 * Service interface for managing meeting room bookings.
 * Provides methods to create, view, and cancel bookings.
 */
public interface BookingService {

    /**
     * Retrieves all bookings for a specific meeting room on a given date.
     *
     * @param room the name or identifier of the meeting room.
     * @param date the date for which bookings should be retrieved.
     * @return a list of {@link Booking} objects for the specified room and date.
     */
    List<Booking> findBookingsByRoomAndDate(String room, LocalDate date);

    /**
     * Creates a new booking for a meeting room.
     *
     * @param room          the name or identifier of the meeting room.
     * @param employeeEmail the email of the employee creating the booking.
     * @param date          the date of the booking.
     * @param timeFrom      the start time of the booking (inclusive).
     * @param timeTo        the end time of the booking (exclusive).
     * @return the created {@link Booking} object.
     * @throws IllegalArgumentException if the booking duration is invalid (less than 1 hour) 
     *                                  or if the end time is before the start time.
     * @throws IllegalStateException    if there is an overlapping booking for the same room, date, and time.
     */
    Booking createBooking(String room, String employeeEmail, LocalDate date, LocalTime timeFrom, LocalTime timeTo);

    /**
     * Cancels an existing booking.
     *
     * @param bookingId the unique identifier of the booking to be canceled.
     * @throws IllegalArgumentException if no booking exists with the specified ID.
     * @throws IllegalStateException    if the booking date is in the past.
     */
    void cancelBooking(Long bookingId);
}


