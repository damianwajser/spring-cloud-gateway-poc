package com.example.demo.server.model;

public class RequestDto {

	private String errorCode;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private String errorMessage;
	private String value;

	public RequestDto(String errorCode, String errorMessage, String value) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.value = value;
	}

	public RequestDto(){}
}
