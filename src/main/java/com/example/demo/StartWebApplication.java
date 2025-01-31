package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class StartWebApplication {

    public static void main(String[] args) {
	System.out.println("in main, starting web app");

        SpringApplication.run(StartWebApplication.class, args);

        System.out.println("Returned from starting web app");
        System.out.println("Point browser to http://localhost:8080/edit");
    }
}
