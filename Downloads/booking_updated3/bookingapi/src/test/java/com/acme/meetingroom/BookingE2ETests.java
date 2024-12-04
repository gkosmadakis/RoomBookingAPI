package com.acme.meetingroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.acme.meetingroom.entity.Booking;
import com.acme.meetingroom.repository.BookingRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class BookingE2ETests {

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private TestRestTemplate restTemplate;

	// Base URL for API endpoints (adjust if needed for port or base path)
	private String BASE_URL = "http://localhost:8081/api/bookings";
	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp() {
		BASE_URL = "http://localhost:" + port + "/api/bookings";
	}

	@Test
	void testEndToEndWorkflow() {
		// Step 1: Create a booking
		String room = "RoomA";
		String employeeEmail = "employee@example.com";
		LocalDate date = LocalDate.now();
		LocalTime timeFrom = LocalTime.of(10, 0);
		LocalTime timeTo = LocalTime.of(11, 0);

		Booking booking = new Booking();
		booking.setRoom(room);
		booking.setEmployeeEmail(employeeEmail);
		booking.setDate(date);
		booking.setTimeFrom(timeFrom);
		booking.setTimeTo(timeTo);

		ResponseEntity<Booking> createResponse = restTemplate.postForEntity(BASE_URL, booking, Booking.class);
		Booking createdBooking = createResponse.getBody();

		assertThat(createResponse.getStatusCode().value()).isEqualTo(201);
		assertThat(createdBooking).isNotNull();
		assertThat(createdBooking.getRoom()).isEqualTo(room);
		assertThat(createdBooking.getEmployeeEmail()).isEqualTo(employeeEmail);

		// Step 2: Retrieve bookings for the room and date
		String getUrl = BASE_URL + "?room=" + room + "&date=" + date;
		@SuppressWarnings("rawtypes")
		ResponseEntity<List> getResponse = restTemplate.getForEntity(getUrl, List.class);

		assertThat(getResponse.getStatusCode().value()).isEqualTo(200);
		assertThat(getResponse.getBody().size()).isEqualTo(1);

		// Step 3: Cancel the booking
		String cancelUrl = BASE_URL + "/" + createdBooking.getId();
		restTemplate.delete(cancelUrl);

		// Step 4: Verify the booking is deleted
		List<Booking> remainingBookings = bookingRepository.findByRoomAndDate("RoomA", LocalDate.now());
		assertThat(remainingBookings).isEmpty();
	}

	@Test
	public void testCreateBookingValidationFailed() {
		// Step 1: Create a booking hat will fail validation (e.g., missing required
		// fields)
		String room = "RoomA";
		String employeeEmail = "";
		LocalDate date = null;
		LocalTime timeFrom = null;
		LocalTime timeTo = null;

		Booking booking = new Booking();
		booking.setRoom(room);
		booking.setEmployeeEmail(employeeEmail);
		booking.setDate(date);
		booking.setTimeFrom(timeFrom);
		booking.setTimeTo(timeTo);

		// Send a POST request with the invalid booking object
		ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, booking, String.class);

		// Verify that the response status is 400 Bad Request
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		// Verify the error message in the body
		assertTrue(response.getBody().contains("Employee email is empty"));
		assertTrue(response.getBody().contains("Date is empty"));
		assertTrue(response.getBody().contains("Time from is empty"));
		assertTrue(response.getBody().contains("Time to is empty"));
	}

	@Test
	void testCreateBookingStartTimeGreaterThanEndTime() {
		// Step 1: Create a booking
		String room = "RoomA";
		String employeeEmail = "employee@example.com";
		LocalDate date = LocalDate.now();
		LocalTime timeFrom = LocalTime.of(11, 0);
		LocalTime timeTo = LocalTime.of(10, 0);

		Booking booking = new Booking();
		booking.setRoom(room);
		booking.setEmployeeEmail(employeeEmail);
		booking.setDate(date);
		booking.setTimeFrom(timeFrom);
		booking.setTimeTo(timeTo);

		ResponseEntity<String> createResponse = restTemplate.postForEntity(BASE_URL, booking, String.class);
		// Assert response
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testCreateBookingWithDurationLessThanOneHour() {
		// Step 1: Create a booking
		String room = "RoomA";
		String employeeEmail = "employee@example.com";
		LocalDate date = LocalDate.now();
		LocalTime timeFrom = LocalTime.of(11, 30);
		LocalTime timeTo = LocalTime.of(12, 0);

		Booking booking = new Booking();
		booking.setRoom(room);
		booking.setEmployeeEmail(employeeEmail);
		booking.setDate(date);
		booking.setTimeFrom(timeFrom);
		booking.setTimeTo(timeTo);

		ResponseEntity<String> createResponse = restTemplate.postForEntity(BASE_URL, booking, String.class);
		// Assert response
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testConcurrentBookingConflict() {
		// Step 1: Create a booking
		String room = "RoomA";
		String employeeEmail1 = "employee1@example.com";
		LocalDate date = LocalDate.now();
		LocalTime timeFrom = LocalTime.of(10, 0);
		LocalTime timeTo = LocalTime.of(11, 0);

		Booking booking = new Booking();
		booking.setRoom(room);
		booking.setEmployeeEmail(employeeEmail1);
		booking.setDate(date);
		booking.setTimeFrom(timeFrom);
		booking.setTimeTo(timeTo);

		ResponseEntity<Booking> createResponse = restTemplate.postForEntity(BASE_URL, booking, Booking.class);
		assertThat(createResponse.getStatusCode().value()).isEqualTo(201);

		// Step 2: Attempt a conflicting booking
		String employeeEmail2 = "employee2@example.com";
		Booking booking2 = new Booking();
		booking2.setRoom(room);
		booking2.setEmployeeEmail(employeeEmail2);
		booking2.setDate(date);
		booking2.setTimeFrom(LocalTime.of(11, 0));
		booking2.setTimeTo(LocalTime.of(13, 0));

		ResponseEntity<String> conflictResponse = restTemplate.postForEntity(BASE_URL, booking2, String.class);
		assertThat(conflictResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(conflictResponse.getBody()).contains("Room is already booked for this slot.");
	}

	@Test
	void testGetBookingInvalidRoom() {
		String room = "";
		LocalDate date = LocalDate.now();

		ResponseEntity<String> getResponse = restTemplate.getForEntity(BASE_URL + "?room=" + room + "&date=" + date,
				String.class);
		// Assert response
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testGetBookingInvalidDate() {
		String room = "RoomA";
		LocalDate date = LocalDate.now().minusDays(1);
		ResponseEntity<String> getResponse = restTemplate.getForEntity(BASE_URL + "?room=" + room + "&date=" + date,
				String.class);
		// Assert response
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testCancelExistentBooking() throws SQLException {
		// Step 1: Create a booking
		String room = "RoomA";
		String employeeEmail = "employee@example.com";
		LocalDate date = LocalDate.now();
		LocalTime timeFrom = LocalTime.of(12, 0);
		LocalTime timeTo = LocalTime.of(13, 0);

		Booking booking = new Booking();
		booking.setRoom(room);
		booking.setEmployeeEmail(employeeEmail);
		booking.setDate(date);
		booking.setTimeFrom(timeFrom);
		booking.setTimeTo(timeTo);

		ResponseEntity<Booking> createResponse = restTemplate.postForEntity(BASE_URL, booking, Booking.class);
		Booking createdBooking = createResponse.getBody();

		// Step 2: Cancel it
		String cancelUrl = BASE_URL + "/" + createdBooking.getId();
		ResponseEntity<String> cancelResponse = restTemplate.exchange(cancelUrl, HttpMethod.DELETE, null, String.class);

		// Assert response
		assertThat(cancelResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void testCancelNonExistentBooking() {
		String cancelUrl = BASE_URL + "/99999"; // Non-existent ID
		ResponseEntity<String> cancelResponse = restTemplate.exchange(cancelUrl, HttpMethod.DELETE, null, String.class);

		// Assert that the response indicates resource not found
		assertThat(cancelResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(cancelResponse.getBody()).contains("Booking not found");
	}

}
