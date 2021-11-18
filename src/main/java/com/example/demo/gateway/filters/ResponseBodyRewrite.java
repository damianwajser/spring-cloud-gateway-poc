package com.example.demo.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.damianwajser.exceptions.model.ExceptionDetail;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public abstract class ResponseBodyRewrite implements RewriteFunction<String, String> {

	private ObjectMapper objectMapper;

	/***
	 * @return returns a collection of values to check against the declared error field (getCheckErrorField)
	 */
	protected abstract Collection<Object> getOkValues();

	protected abstract String getErrorMessageField();

	/***
	 * @return field used to know if there was an error
	 */
	protected abstract String getCheckErrorField();

	/**
	 * @return Collection of fields that will be omitted in the response when the real server answers correctly
	 */
	protected abstract Set<String> getUnusedFieldsInHappyPath();

	protected abstract ExceptionDetail getMessageErrorCommunication();

	public ResponseBodyRewrite(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Publisher<String> apply(ServerWebExchange serverWebExchange, String body) {
		try {
			//check if real server return http code ok
			if (serverWebExchange.getResponse().getStatusCode().is2xxSuccessful()) {
				//then build response ok comunication level
				return communicationOk(serverWebExchange, body);
			} else {
				// if real server return error, write error communication message
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
		return Mono.just(objectMapper.writeValueAsString(getMessageErrorCommunication()));
	}

	private ExceptionDetail createError(ServerWebExchange serverWebExchange, Map<String, Object> map) {
		return new ExceptionDetail((String) map.get(getCheckErrorField()), (String) map.get(getErrorMessageField()), Optional.empty());
	}

	private Map<String, Object> removeUnusedFields(Map<String, Object> map) {
		map.keySet().removeAll(getUnusedFieldsInHappyPath());
		return map;
	}

}
