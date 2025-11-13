package com.example.back

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
class BackApplication {
    static void main(String[] args) {
        SpringApplication.run(BackApplication, args)
    }
}
