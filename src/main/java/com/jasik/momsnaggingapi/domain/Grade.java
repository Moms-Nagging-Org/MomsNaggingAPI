package com.jasik.momsnaggingapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int gradeLevel;
    private int createdYear;
    private int createdWeek;

    @Builder
    public Grade(int gradeLevel, int createdYear, int createdWeek) {
        this.gradeLevel = gradeLevel;
        this.createdYear = createdYear;
        this.createdWeek = createdWeek;
    }

}
