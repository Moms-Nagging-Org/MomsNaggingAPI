package com.jasik.momsnaggingapi.domain.schedule;

import com.jasik.momsnaggingapi.domain.common.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ScheduleCategory extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long categoryId;
    private Long scheduleId;

    @Builder
    public ScheduleCategory(Long categoryId, Long scheduleId) {

        this.categoryId = categoryId;
        this.scheduleId = scheduleId;
    }
}
