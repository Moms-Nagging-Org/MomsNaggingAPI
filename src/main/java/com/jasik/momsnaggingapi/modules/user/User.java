package com.jasik.momsnaggingapi.modules.user;

import lombok.*;

import javax.persistence.*;
import static javax.persistence.GenerationType.AUTO;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    private String username;

    @Column(length = 100)
    private String password;
}
