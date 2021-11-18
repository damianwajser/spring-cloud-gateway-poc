package com.example.demo.gateway;

import com.example.demo.gateway.filters.impl.BancoRerwitter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfiguration {
	@LocalServerPort
	private int port;

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder, ObjectMapper objectMapper) {
		return builder.routes()
				.route("lala", r -> r.path("/error")
						.filters(f -> f.rewritePath("/error", "/error_controller")
								.modifyResponseBody(String.class, String.class, new BancoRerwitter(objectMapper)))
						.uri("http://localhost:" + port))
				.route("lala1", r -> r.path("/ok")
						.filters(f -> f.rewritePath("/ok", "/ok_controller")
								.modifyResponseBody(String.class, String.class, new BancoRerwitter(objectMapper)))
						.uri("http://localhost:" + port))
				.route("lala2", r -> r.path("/communication_error")
						.filters(f -> f.rewritePath("/communication_error", "/comunication_error_controller")
								.modifyResponseBody(String.class, String.class, new BancoRerwitter(objectMapper)))
						.uri("http://localhost:" + port))
				.build();
	}
}
