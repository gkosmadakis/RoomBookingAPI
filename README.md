**Room Booking API**

How to run it:

After downloading from github do a mvn clean install

To start the project run mvn spring-boot:run

To test it you can use Postman or similar application.

1. To get a room booking http://localhost:8080/api/bookings?room={room}&date={date}
2. To create a new room booking http://localhost:8080/api/bookings Use a json body { "room":"Room A",
 "employeeEmail": "test@email.com",
 "date": "2024-12-05",
 "timeFrom": "10:00",
 "timeTo":"11:00"
}
3. To delete a room booking http://localhost:8080/api/bookings/{id}
4. Also there are Junit test and E2E Tests that you can run
