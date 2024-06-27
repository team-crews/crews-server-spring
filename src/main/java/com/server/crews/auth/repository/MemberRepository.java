package com.server.crews.auth.repository;

import com.server.crews.auth.domain.Member;
import com.server.crews.recruitment.domain.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    @Query("""
            SELECT m FROM Member m
            WHERE m.email = :email AND m.recruitment = :recruitment
             """)
    Optional<Member> findByEmailAndRecruitment(@Param("email") String email, @Param("recruitment") Recruitment recruitment);
}
