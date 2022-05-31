package com.jasik.momsnaggingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class MomsNaggingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MomsNaggingApiApplication.class, args);
    }

}
