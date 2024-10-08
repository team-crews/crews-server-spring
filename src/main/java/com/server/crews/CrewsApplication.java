package com.server.crews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CrewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrewsApplication.class, args);
    }
}
