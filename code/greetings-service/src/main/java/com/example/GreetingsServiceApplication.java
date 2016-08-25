package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@SpringBootApplication
public class GreetingsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreetingsServiceApplication.class, args);
	}

	@RestController
	class GreetingsRestController {

		@RequestMapping(value = "/greet")
		Greet greet() {
			return new Greet("Hello World!");
		}

	}

	@Data
	class Greet {
		private final String greeting;
	}

}
