package com.acme.meetingroom.dto;

import java.time.LocalDateTime;

/**
 * Represents a standardized error response for API clients.
 */
public class ErrorResponse {

	private int status;
	private String message;
	private LocalDateTime timestamp;

	/**
	 * Constructs an ErrorResponse instance.
	 *
	 * @param status  the HTTP status code
	 * @param message the error message
	 */
	public ErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}

	// Getters and setters
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "ErrorResponse{" + "status=" + status + ", message='" + message + '\'' + ", timestamp=" + timestamp
				+ '}';
	}
}
