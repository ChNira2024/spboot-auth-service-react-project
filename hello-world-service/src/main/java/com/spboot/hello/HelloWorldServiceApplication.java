package com.spboot.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.spboot.hello")
public class HelloWorldServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldServiceApplication.class, args);
	}

}
