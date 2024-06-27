package com.server.crews.auth.domain;

import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import com.server.crews.recruitment.domain.Recruitment;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Entity
@Table(name = "member", uniqueConstraints = @UniqueConstraint(columnNames = {"recruitment_id", "email"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "recruitment_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Recruitment recruitment;

    public Member(String email, String password, Role role, Recruitment recruitment) {
        this(null, email, password, role, recruitment);
    }

    public Member(Long id, String email, String password, Role role, Recruitment recruitment) {
        validateEmail(email);
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.recruitment = recruitment;
    }

    private void validateEmail(String email) {
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            throw new CrewsException(ErrorCode.INVALID_EMAIL_PATTERN);
        }
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public Recruitment getRecruitment() {
        return recruitment;
    }
}
