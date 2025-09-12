// src/main/java/com/example/swnuclearfusionwas/config/WebPushConfig.java
package com.example.swnuclearfusionwas.config;

import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class WebPushConfig {

    @Value("${webpush.vapid.public}")
    private String publicKeyBase64Url;

    @Value("${webpush.vapid.private}")
    private String privateKeyBase64Url;

    @Value("${webpush.subject:mailto:admin@example.com}")
    private String subject;

    @Bean
    public PushService webPushClient() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        PushService client = new PushService();
        client.setPublicKey(Utils.loadPublicKey(publicKeyBase64Url));
        client.setPrivateKey(Utils.loadPrivateKey(privateKeyBase64Url));
        client.setSubject(subject);
        return client;
    }
}