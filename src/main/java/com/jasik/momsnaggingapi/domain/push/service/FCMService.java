package com.jasik.momsnaggingapi.domain.push.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.jasik.momsnaggingapi.domain.push.FCM;
import com.jasik.momsnaggingapi.domain.schedule.Schedule.SchedulePush;
import com.jasik.momsnaggingapi.domain.schedule.repository.ScheduleRepository;
import com.jasik.momsnaggingapi.infra.common.AsyncService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMService {

    private final ObjectMapper objectMapper;
    private final ScheduleRepository scheduleRepository;
    private final AsyncService asyncService;
    @Value("${fcm.project-id}")
    private String PROJECT_ID;

    @Scheduled(initialDelay = 1000, fixedRate = 60000)
    @Async
    public void fixedRateJob() {
        LocalTime nowTime = LocalTime.now();
        // 5분 단위로 수행
        if (nowTime.getMinute() % 5 == 0) {
//            sendSchedulePush(nowTime);
        }
    }

    private void sendSchedulePush(LocalTime nowTime) {
        LocalDate pushDate = LocalDate.now();
        LocalTime pushTime = LocalTime.of(nowTime.getHour(), nowTime.getMinute(), 0);

        List<SchedulePush> schedulePushes = scheduleRepository.findSchedulePushByScheduleDateAndAlarmTime(
            pushDate, pushTime);
        log.info("Push Count: " + schedulePushes.size());
        schedulePushes.forEach(this::sendAsyncMessage);
    }

    private void sendAsyncMessage(SchedulePush push) {
        try {
            asyncService.run(() -> {
                try {
                    sendMessageTo(push.getTargetToken(), push.getTitle(),
                            String.format("%s, %s", push.getNickName(), push.getBody()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RejectedExecutionException e) {
            log.error("FCM Scheduler Thread was fulled");
        }
    }

    // FCM message 전송 함수
    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/v1/projects/" + PROJECT_ID + "/messages:send")
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        try (AutoCloseable ignored = client.newCall(request).execute()) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // FCM message 생성, String 반환
    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FCM fcm = FCM.builder()
                .message(FCM.Message.builder()
                        .token(targetToken)
                        .notification(FCM.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        )
                        .build()
                )
                .validate_only(false)
                .build();

        return objectMapper.writeValueAsString(fcm);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebase-key.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

}
