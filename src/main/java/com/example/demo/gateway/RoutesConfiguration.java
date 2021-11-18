package com.example.demo.gateway;

import com.example.demo.gateway.filters.impl.BancoRerwiter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class RoutesConfiguration {
	@LocalServerPort
	private int port;

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder, ObjectMapper objectMapper) {
		return builder.routes()
				.route("id1", configureRoute(objectMapper, "/error", "/error_controller"))
				.route("id2", configureRoute(objectMapper, "/ok", "/ok_controller"))
				.route("id2", configureRoute(objectMapper, "/communication_error", "/comunication_error_controller"))
				.build();
	}

	@NotNull
	private Function<PredicateSpec, Buildable<Route>> configureRoute(ObjectMapper objectMapper, String path, String realPath) {
		return r -> r.path(path)
				.filters(f -> f.rewritePath(path, realPath)
						.modifyResponseBody(String.class, String.class, new BancoRerwiter(objectMapper)))
				.uri("http://localhost:" + port);
	}
}
