package com.jasik.momsnaggingapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Grade extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;

    private int gradeLevel;
    private int createdYear;
    private int createdWeek;

    @Builder
    public Grade(Long userId, int gradeLevel, int createdYear, int createdWeek) {
        this.userId = userId;
        this.gradeLevel = gradeLevel;
        this.createdYear = createdYear;
        this.createdWeek = createdWeek;
    }

}
