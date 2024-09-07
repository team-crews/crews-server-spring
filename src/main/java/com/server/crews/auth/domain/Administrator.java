package com.server.crews.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
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

    @Size(max = 30, message = "동아리 이름은 30자 이하입니다.")
    @Column(name = "club_name", nullable = false, unique = true, length = 30)
    private String clubName;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    public Administrator(String clubName, String password) {
        this.clubName = clubName;
        this.password = password;
    }
}
