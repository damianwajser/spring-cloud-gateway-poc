package com.example.demo.gateway.filters.impl;

import com.example.demo.gateway.filters.ResponseBodyRewrite;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BancoRerwitter extends ResponseBodyRewrite {

	public BancoRerwitter(ObjectMapper objectMapper) {
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
	protected Map<String, Object> getMessageErrorCommunication() {
		return Map.of("errorCode", "xxxx", "errorMessage", "se rompio");
	}
}
