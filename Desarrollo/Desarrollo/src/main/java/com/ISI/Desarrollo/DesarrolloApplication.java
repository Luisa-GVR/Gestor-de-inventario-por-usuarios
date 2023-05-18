package com.ISI.Desarrollo;

import com.ISI.Desarrollo.Database.Methods;
import com.ISI.Desarrollo.Database.MyService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class DesarrolloApplication {

	public static void main(String[] args) {

		SpringApplication.run(DesarrolloApplication.class, args);
	}

}
