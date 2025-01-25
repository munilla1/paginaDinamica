package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaginaDinamicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaginaDinamicaApplication.class, args);
        System.out.println("🚀 Aplicación Spring Boot iniciada en http://localhost:8080/");
	}

}
