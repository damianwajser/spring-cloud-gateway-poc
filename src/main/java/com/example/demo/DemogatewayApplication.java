package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
public class DemogatewayApplication {

	@LocalServerPort
	private int port;

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder, ObjectMapper objectMapper) {
		return builder.routes()
				.route("lala", r -> r.path("/error")
						.filters(f -> f.rewritePath("/error","/error_controller")
								.modifyResponseBody(String.class, String.class, new ResponseBodyRewrite(objectMapper, Arrays.asList("error"))))
						.uri("http://localhost:"+port))
				.route("lala1", r -> r.path("/ok")
						.filters(f -> f.rewritePath("/ok","/ok_controller")
								.modifyResponseBody(String.class, String.class, new ResponseBodyRewrite(objectMapper, Arrays.asList("error"))))
						.uri("http://localhost:"+port))
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemogatewayApplication.class, args);
	}
}
