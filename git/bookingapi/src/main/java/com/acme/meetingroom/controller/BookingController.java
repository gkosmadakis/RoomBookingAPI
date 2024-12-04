package com.acme.meetingroom.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acme.meetingroom.dto.ErrorResponse;
import com.acme.meetingroom.entity.Booking;
import com.acme.meetingroom.exception.BookingNotFoundException;
import com.acme.meetingroom.service.BookingService;
import com.acme.meetingroom.utils.BookingValidator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * REST controller for managing meeting room bookings. Handles client requests
 * and delegates business logic to the {@link BookingService}.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

	private final BookingService bookingService;

	private final BookingValidator bookingValidator;

	/**
	 * Constructs a new instance of {@link BookingController}.
	 *
	 * @param bookingService the service used to manage booking operations.
	 */
	public BookingController(BookingService bookingService, BookingValidator bookingValidator) {
		this.bookingService = bookingService;
		this.bookingValidator = bookingValidator;
	}

	/**
	 * Retrieves all bookings for a specific meeting room on a given date.
	 *
	 * @param room the name or identifier of the meeting room.
	 * @param date the date for which bookings should be retrieved (in ISO-8601
	 *             format, e.g., "2024-11-29").
	 * @return a list of bookings for the specified room and date.
	 */
	@Operation(summary = "Retrieve bookings by room and date", description = "Fetch all bookings for a specific meeting room on a given date. Provide the room name and the desired date in ISO-8601 format.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Bookings retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content(mediaType = "application/json")) })
	@GetMapping
	public ResponseEntity<?> findBookingsByRoomAndDate(@RequestParam String room,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		Map<String, String> errors = bookingValidator.validateRoomAndDate(room, date);
		if (errors != null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}
		List<Booking> bookings = bookingService.findBookingsByRoomAndDate(room, date);
		return ResponseEntity.ok(bookings);
	}

	/**
	 * Creates a new booking for a meeting room with conflict detection.
	 *
	 * @param booking the booking object.
	 * @return the created booking.
	 * @throws IllegalArgumentException if the input parameters are invalid.
	 * @throws IllegalStateException    if the room is already booked for the
	 *                                  specified time range.
	 */
	@Operation(summary = "Create a new booking", description = "Create a booking for a specific meeting room. This operation checks for time conflicts and ensures the room is available.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Booking created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "409", description = "Conflict: Room already booked for the specified time range", content = @Content(mediaType = "application/json")) })
	@PostMapping
	public ResponseEntity<?> createBooking(@RequestBody Booking booking, BindingResult bindingResult) {
		Map<String, String> errors = bookingValidator.validateBooking(booking);
		if (errors.size() > 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}
		try {
			Booking bookingToCreate = bookingService.createBooking(booking.getRoom(), booking.getEmployeeEmail(),
					booking.getDate(), booking.getTimeFrom(), booking.getTimeTo());
			return ResponseEntity.status(HttpStatus.CREATED).body(bookingToCreate);
		} catch (IllegalStateException e) {
			// If there's a booking conflict, return a 409 Conflict status with an error
			// message
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage()));
		} catch (IllegalArgumentException e) {
			// If the booking duration is invalid, return a 400 Bad Request status
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	/**
	 * Cancels an existing booking by its unique identifier.
	 *
	 * @param bookingId the ID of the booking to cancel.
	 * @return a response indicating success or failure.
	 * @throws SQLException
	 * @throws IllegalStateException    if the booking is in the past.
	 * @throws BookingNotFoundException if no booking exists with the specified ID.
	 */
	@Operation(summary = "Cancel a booking", description = "Cancel a booking using its unique identifier. This operation only allows canceling future bookings.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Booking canceled successfully", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid operation: Cannot cancel past bookings", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "404", description = "Booking not found", content = @Content(mediaType = "application/json")) })
	@DeleteMapping("/{id}")
	public ResponseEntity<?> cancelBooking(@PathVariable Long id) throws SQLException {
		try {
			bookingService.cancelBooking(id);
			return ResponseEntity.noContent().build(); // 204 No Content
		} catch (IllegalStateException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
		} catch (BookingNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
		}
	}

}
