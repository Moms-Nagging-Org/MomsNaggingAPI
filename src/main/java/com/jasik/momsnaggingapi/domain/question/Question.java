package com.jasik.momsnaggingapi.domain.question;

import com.jasik.momsnaggingapi.domain.common.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Question extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String answer;
    @Column(columnDefinition = "boolean default false")
    private boolean isFreq;

    @Builder
    public Question(String title, String answer, boolean isFreq) {
        this.title = title;
        this.answer = answer;
        this.isFreq = isFreq;
    }

}
