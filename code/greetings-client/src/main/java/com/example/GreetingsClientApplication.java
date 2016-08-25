package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GreetingsClientApplication {

	// For zipkin to send trace on every request
	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}

	@LoadBalanced
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(GreetingsClientApplication.class, args);
	}

	@RestController
	@RequestMapping("/api")
	class GreetingsRestController {

		@Autowired
		RestTemplate restTemplate;

		@Autowired
		GreetingsServiceClient client;

		@RequestMapping(value = "/greet")
		String greet() {
			// return
			// restTemplate.getForObject("http://greetings-service/greet",
			// String.class);
			return client.getGreet();
		}

	}

	@FeignClient("greetings-service")
	interface GreetingsServiceClient {
		@RequestMapping(method = RequestMethod.GET, value = "/greet")
		String getGreet();
	}

}
