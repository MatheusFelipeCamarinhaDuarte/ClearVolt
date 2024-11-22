package com.fiap.m4g.clearvolt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EntityScan
@ComponentScan
@SpringBootApplication
public class ClearVoltApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClearVoltApplication.class, args);
	}

}
