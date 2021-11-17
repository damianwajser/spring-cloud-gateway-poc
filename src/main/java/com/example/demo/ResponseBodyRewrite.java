package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ResponseBodyRewrite implements RewriteFunction<String, String> {

	private ObjectMapper objectMapper;


	public ResponseBodyRewrite(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Publisher<String> apply(ServerWebExchange serverWebExchange, String body) {
		try {
			if (serverWebExchange.getResponse().getStatusCode().equals(HttpStatus.OK)) {
				return communicationOk(serverWebExchange, body);
			} else {
				//TODO:escribir error del servicio externo
				serverWebExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
				return Mono.just(objectMapper.writeValueAsString(Map.of("error", "se rompio")));
			}
		} catch (IOException e) {
			e.printStackTrace();
			serverWebExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			return Mono.just("parsing error");
		}
	}

	private Mono<String> communicationOk(ServerWebExchange serverWebExchange, String body) throws JsonProcessingException {
		Map<String, Object> map = objectMapper.readValue(body, Map.class);
		if (map.get("errorCode").equals("0000")) {
			map = removeUnusedFields(map);
		} else {
			map = createError(map);
			serverWebExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
		}
		return Mono.just(objectMapper.writeValueAsString(map));
	}

	private Map<String, Object> createError(Map<String, Object> map) {
		Map<String, Object> response = new HashMap<>();
		response.put("new_error", map.get("errorMessage"));
		return response;
	}

	private Map<String, Object> removeUnusedFields(Map<String, Object> map) {
		map.remove("errorCode");
		return map;
	}
}
