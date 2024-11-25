package com.proempresa.campaniamodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CampaniaModuleEntityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampaniaModuleEntityApplication.class, args);
	}

}
