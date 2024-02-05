package com.server.crews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
public class CrewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrewsApplication.class, args);
    }

}
