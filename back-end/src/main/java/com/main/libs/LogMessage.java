package com.main.libs;

public class LogMessage {
	private String message;
	private Status status;

	public LogMessage(String message, Status status) {
		this.message = message;
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public Status getStatus() {
		return status;
	}
}
