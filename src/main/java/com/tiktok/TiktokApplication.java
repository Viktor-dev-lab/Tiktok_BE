package com.tiktok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TiktokApplication {
    public static void main(String[] args) {
        SpringApplication.run(TiktokApplication.class, args);
    }
}
