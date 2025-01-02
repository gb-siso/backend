package com.guenbon.siso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class SisoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SisoApplication.class, args);
	}

}

