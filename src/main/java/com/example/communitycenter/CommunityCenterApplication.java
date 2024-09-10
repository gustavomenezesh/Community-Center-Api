package com.example.communitycenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.communitycenter")
public class CommunityCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityCenterApplication.class, args);
    }
}
