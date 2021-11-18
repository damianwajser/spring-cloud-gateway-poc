package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.damianwajser.exceptions.model.ErrorMessage;
import com.github.damianwajser.exceptions.model.ExceptionDetail;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;

public abstract class ResponseBodyRewrite implements RewriteFunction<String, String> {

	private ObjectMapper objectMapper;

	protected abstract Collection<Object> getOkValues();

	protected abstract String getErrorMessageField();

	protected abstract String getCheckErrorField();

	protected abstract Set<String> getUnusedFieldsInHappyPath();

	protected abstract Map<String, Object> getMessageErrorCommunication();


	public ResponseBodyRewrite(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Publisher<String> apply(ServerWebExchange serverWebExchange, String body) {
		try {
			if (serverWebExchange.getResponse().getStatusCode().equals(HttpStatus.OK)) {
				return communicationOk(serverWebExchange, body);
			} else {
				serverWebExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
				return communicationError(serverWebExchange, body);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			serverWebExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			return Mono.just("parsing error");
		}
	}

	private Mono<String> communicationOk(ServerWebExchange serverWebExchange, String body) throws JsonProcessingException {
		Map<String, Object> map = objectMapper.readValue(body, Map.class);
		Object valueToCheck = map.get(getCheckErrorField());
		if (getOkValues().contains(valueToCheck)) {
			map = removeUnusedFields(map);
			return Mono.just(objectMapper.writeValueAsString(map));
		} else {
			serverWebExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
			return Mono.just(objectMapper.writeValueAsString(createError(serverWebExchange, map)));
		}
	}



	private Mono<String> communicationError(ServerWebExchange serverWebExchange, String body) throws JsonProcessingException {
		serverWebExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		return Mono.just(objectMapper.writeValueAsString(createError(serverWebExchange, getMessageErrorCommunication())));
	}


	private ExceptionDetail createError(ServerWebExchange serverWebExchange, Map<String, Object> map) {
		ExceptionDetail detail = new ExceptionDetail((String) map.get(getCheckErrorField()), (String) map.get(getErrorMessageField()), Optional.empty());
		return detail;
	}

	private Map<String, Object> removeUnusedFields(Map<String, Object> map) {
		map.keySet().removeAll(getUnusedFieldsInHappyPath());
		return map;
	}

}
