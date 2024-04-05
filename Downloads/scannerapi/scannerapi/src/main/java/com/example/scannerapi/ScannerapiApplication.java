package com.example.scannerapi;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ScannerapiApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(ScannerapiApplication.class);
    }
	  
	public static void main(String[] args) {
		SpringApplication.run(ScannerapiApplication.class, args);
	}

	@PostConstruct
    void started() {
        // set JVM timezone as UTC
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
