package com.gmail.detection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GmailManagementApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                GmailManagementApplication.class,
                args
        );

    }

}