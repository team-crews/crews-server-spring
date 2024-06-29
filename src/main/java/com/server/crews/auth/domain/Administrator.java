package com.server.crews.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "administrator")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Administrator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "club_name", nullable = false, unique = true)
    private String clubName;

    @Column(name = "password", nullable = false)
    private String password;

    public Administrator(String clubName, String password) {
        this.clubName = clubName;
        this.password = password;
    }
}
