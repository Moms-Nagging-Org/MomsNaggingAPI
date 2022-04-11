package com.jasik.momsnaggingapi.domain.nagging;

import com.jasik.momsnaggingapi.domain.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Nagging extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String custom;
    @Column(columnDefinition = "TEXT")
    private String level1;
    @Column(columnDefinition = "TEXT")
    private String level2;
    @Column(columnDefinition = "TEXT")
    private String level3;

    @Builder
    public Nagging(Long userId, String custom, String level1, String level2, String level3) {
        this.userId = userId;
        this.custom = custom;
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
    }

}
