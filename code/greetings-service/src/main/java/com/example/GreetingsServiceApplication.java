package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@EnableDiscoveryClient
@SpringBootApplication
public class GreetingsServiceApplication {

	// For zipkin to send trace on every request
	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}

	public static void main(String[] args) {
		SpringApplication.run(GreetingsServiceApplication.class, args);
	}

	@RefreshScope
	@RestController
	class GreetingsRestController {

		@Value("${greet.value}")
		private String greetValue;

		@RequestMapping(value = "/greet")
		Greet greet() {
			return new Greet(greetValue);
		}

	}

	@Data
	class Greet {
		private final String greeting;
	}

}
