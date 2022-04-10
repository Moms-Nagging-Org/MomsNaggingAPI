package com.jasik.momsnaggingapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Push {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int pushDay;
    private LocalDateTime pushTime;

    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "boolean default false")
    private boolean isMon;
    @Column(columnDefinition = "boolean default false")
    private boolean isTue;
    @Column(columnDefinition = "boolean default false")
    private boolean isWed;
    @Column(columnDefinition = "boolean default false")
    private boolean isThu;
    @Column(columnDefinition = "boolean default false")
    private boolean isFri;
    @Column(columnDefinition = "boolean default false")
    private boolean isSat;
    @Column(columnDefinition = "boolean default false")
    private boolean isSun;

    @Builder
    public Push(int pushDay, LocalDateTime pushTime, String content,
                boolean isMon, boolean isTue, boolean isWed, boolean isThu,
                boolean isFri, boolean isSat, boolean isSun) {
        this.pushDay = pushDay;
        this.pushTime = pushTime;
        this.content = content;
        this.isMon = isMon;
        this.isTue = isTue;
        this.isWed = isWed;
        this.isThu = isThu;
        this.isFri = isFri;
        this.isSat = isSat;
        this.isSun = isSun;
    }

}
