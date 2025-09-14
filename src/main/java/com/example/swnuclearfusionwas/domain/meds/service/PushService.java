package com.example.swnuclearfusionwas.domain.meds.service;

import com.example.swnuclearfusionwas.domain.meds.entity.PushSubscription;
import com.example.swnuclearfusionwas.domain.meds.repository.PushSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.Notification;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushService {

    private final nl.martijndwars.webpush.PushService webPushClient;
    private final PushSubscriptionRepository pushRepo;

    @Value("${webpush.vapid.public}")
    private String publicKeyBase64Url;

    public void sendPush(PushSubscription sub, String title, String body, String url) {
        try {
            String payload = """
                {"title":"%s","body":"%s","url":"%s"}
                """.formatted(title, body, url);

            Notification notification = new Notification(
                    sub.getEndpoint(),
                    sub.getP256dh(),
                    sub.getAuth(),
                    payload
            );

            HttpResponse response = webPushClient.send(notification);
            int code = response.getStatusLine().getStatusCode();

            if (code == 410 || code == 404) {
                pushRepo.delete(sub);
                System.out.println("Removed expired subscription: " + sub.getEndpoint());
            } else {
                System.out.println("Push sent: " + code + " -> " + sub.getEndpoint());
            }
        } catch (Exception e) {
            System.err.println("Push send error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getPublicKey() {
        return publicKeyBase64Url;
    }
}