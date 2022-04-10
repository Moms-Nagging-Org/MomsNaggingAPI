package com.jasik.momsnaggingapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Schedule extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;

    private int originalId;
    private int seqNumber;
    private int goalCount;
    private int doneCount;

    private String title;

    private LocalDateTime scheduleTime;
    private LocalDateTime scheduleDate;
    private LocalDateTime alarmTime;
    private LocalDateTime routineEndDate;

    @Column(columnDefinition = "boolean default false")
    private boolean isTemplate;
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
    public Schedule(Long userId, int originalId, int seqNumber, int goalCount, int doneCount, String title,
                    LocalDateTime scheduleTime, LocalDateTime scheduleDate, LocalDateTime alarmTime,
                    LocalDateTime routineEndDate, boolean isTemplate, boolean isMon, boolean isTue,
                    boolean isWed, boolean isThu, boolean isFri, boolean isSat, boolean isSun) {
        this.userId = userId;
        this.originalId = originalId;
        this.seqNumber = seqNumber;
        this.goalCount = goalCount;
        this.doneCount = doneCount;
        this.title = title;
        this.scheduleTime = scheduleTime;
        this.scheduleDate = scheduleDate;
        this.alarmTime = alarmTime;
        this.routineEndDate = routineEndDate;
        this.isTemplate = isTemplate;
        this.isMon = isMon;
        this.isTue = isTue;
        this.isWed = isWed;
        this.isThu = isThu;
        this.isFri = isFri;
        this.isSat = isSat;
        this.isSun = isSun;
    }

}
