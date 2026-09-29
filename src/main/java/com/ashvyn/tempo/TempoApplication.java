package com.ashvyn.tempo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
public class TempoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TempoApplication.class, args);
	}

}
