package com.acme.meetingroom.repository;

import com.acme.meetingroom.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository for managing {@link Booking} entities.
 * Extends {@link JpaRepository} to provide basic CRUD operations and query methods.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Finds all bookings for a specific room and date.
     *
     * @param room the name or identifier of the meeting room.
     * @param date the date for which bookings should be retrieved.
     * @return a list of bookings for the specified room and date.
     */
    List<Booking> findByRoomAndDate(String room, LocalDate date);

    /**
     * Checks if a booking exists for a specific room, date, and time range.
     *
     * @param room     the name or identifier of the meeting room.
     * @param date     the date of the booking.
     * @param timeTo   the end time of the booking (exclusive).
     * @param timeFrom the start time of the booking (inclusive).
     * @return true if an overlapping booking exists; false otherwise.
     */
    boolean existsByRoomAndDateAndTimeFromLessThanEqualAndTimeToGreaterThanEqual(
            String room, LocalDate date, LocalTime timeTo, LocalTime timeFrom);
}


