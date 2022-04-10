package com.jasik.momsnaggingapi.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Release extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String version;
    @Column(columnDefinition = "boolean default true")
    private boolean isRequired;

    @Builder
    public Release(String version, boolean isRequired) {
        this.version = version;
        this.isRequired = isRequired;
    }

}
