package com.example.demo;

import com.example.demo.model.RequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.damianwajser.exceptions.model.ExceptionDetail;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class DemoApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private ObjectMapper objectMapper;

	private RestTemplate restTemplate = new RestTemplate();

	@Test
	void server_response_error_code() throws JsonProcessingException {
		try {
			ResponseEntity<Map> response = restTemplate.getForEntity("http://localhost:" + port + "/error", Map.class);
			Assertions.fail("no debe pasar por aquii");
		} catch (HttpClientErrorException.BadRequest ex) {
			ExceptionDetail detail = objectMapper.readValue(ex.getResponseBodyAsString(), ExceptionDetail.class);
			assertThat(detail.getErrorCode()).isEqualTo("0001");
			assertThat(detail.getErrorMessage()).isEqualTo("error");
		}
	}

	@Test
	void server_response_ok() throws JsonProcessingException {
		ResponseEntity<MessageDto> response = restTemplate.getForEntity("http://localhost:" + port + "/ok", MessageDto.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getValue()).isEqualTo("value");
	}
	@Test
	void server_response_error_comunication() throws JsonProcessingException {
		try {
			ResponseEntity<Map> response = restTemplate.getForEntity("http://localhost:" + port + "/communication_error", Map.class);
			Assertions.fail("no debe pasar por aquii");
		} catch (HttpServerErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
			ExceptionDetail detail = objectMapper.readValue(ex.getResponseBodyAsString(), ExceptionDetail.class);
			assertThat(detail.getErrorCode()).isEqualTo("xxxx");
			assertThat(detail.getErrorMessage()).isEqualTo("se rompio");
		}
	}

}
