package com.example.demogateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

public class ResponseBodyRewrite implements RewriteFunction<String, String> {

	private ObjectMapper objectMapper;

	public ResponseBodyRewrite(ObjectMapper objectMapper) {

		this.objectMapper = objectMapper;
	}


	@Override
	public Publisher<String> apply(ServerWebExchange serverWebExchange, String body) {
		try {
			Map<String, Object> map = objectMapper.readValue(body, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
