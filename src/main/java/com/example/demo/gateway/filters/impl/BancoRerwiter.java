package com.example.demo.gateway.filters.impl;

import com.example.demo.gateway.filters.ResponseBodyRewrite;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.damianwajser.exceptions.model.ExceptionDetail;

import java.util.*;

public class BancoRerwiter extends ResponseBodyRewrite {

	public BancoRerwiter(ObjectMapper objectMapper) {
		super(objectMapper);
	}

	@Override
	protected Collection<Object> getOkValues() {
		return List.of("0000");
	}

	@Override
	protected String getErrorMessageField() {
		return "errorMessage";
	}

	@Override
	protected String getCheckErrorField() {
		return "errorCode";
	}

	@Override
	protected Set<String> getUnusedFieldsInHappyPath() {
		return Set.of("errorCode", "errorMessage");
	}

	@Override
	protected ExceptionDetail getMessageErrorCommunication() {
		return new ExceptionDetail("xxxx", "se rompio", Optional.empty());
	}
}
