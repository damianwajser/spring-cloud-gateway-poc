package com.example.demo.server.controllers;

import com.example.demo.server.model.RequestDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	@GetMapping("/error_controller")
	public RequestDto error (){
		return new RequestDto("0001","error","value");
	}

	@GetMapping("/ok_controller")
	public RequestDto ok (){
		return new RequestDto("0000","no error","value");
	}

	@GetMapping("/comunication_error_controller")
	public RequestDto comunicationError (){
		throw new RuntimeException("rompi");
	}
}
