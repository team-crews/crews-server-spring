package com.server.crews.auth.domain;

import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.GeneralErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "applicant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Applicant {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    public Applicant(String email, String password) {
        validateEmail(email);
        this.email = email;
        this.password = password;
    }

    private void validateEmail(String email) {
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            throw new CrewsException(GeneralErrorCode.INVALID_EMAIL_PATTERN);
        }
    }
}
