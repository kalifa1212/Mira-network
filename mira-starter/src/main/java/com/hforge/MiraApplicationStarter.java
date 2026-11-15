package com.hforge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.hforge",})

public class MiraApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(MiraApplicationStarter.class, args);
    }
}