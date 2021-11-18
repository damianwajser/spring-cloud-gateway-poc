package com.example.demo;

public class MessageDto {

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private String value;

	public MessageDto(String value) {
		this.value = value;
	}

	public MessageDto() {
	}
}
