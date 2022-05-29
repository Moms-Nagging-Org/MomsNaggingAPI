package com.jasik.momsnaggingapi.domain.fcm.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.sun.tools.javac.util.List;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class FCMService {
    private String getAccessToken() throws IOException {
        String firebaseConfig = "firebase/firebase-key.json";
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfig).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
