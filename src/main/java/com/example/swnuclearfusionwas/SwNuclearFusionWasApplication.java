package com.example.swnuclearfusionwas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.scheduling.annotation.EnableScheduling;  // 매 분마다 스케줄링 확인

import java.security.Security;

@SpringBootApplication
@EnableScheduling
public class SwNuclearFusionWasApplication {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        SpringApplication.run(SwNuclearFusionWasApplication.class, args);
    }

}
