package com.jasik.momsnaggingapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Diary extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;

    private String title;
    private String context;
    private LocalDateTime diaryDate;

    @Builder
    public Diary(Long userId, String title, String context, LocalDateTime diaryDate) {
        this.userId = userId;
        this.title = title;
        this.context = context;
        this.diaryDate = diaryDate;
    }

}
