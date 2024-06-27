package com.server.crews.auth.application;

import com.server.crews.auth.dto.request.AdminLoginRequest;
import com.server.crews.auth.dto.request.ApplicantLoginRequest;
import com.server.crews.auth.dto.response.AccessTokenResponse;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import com.server.crews.auth.domain.Member;
import com.server.crews.auth.repository.MemberRepository;
import com.server.crews.auth.domain.Role;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AccessTokenResponse loginForAdmin(AdminLoginRequest request) {
        String email = request.email();
        String password = request.password();

        Member member = memberRepository.findByEmail(email).orElseGet(() -> createAdmin(email, password));
        String accessToken = jwtTokenProvider.createAccessToken(Role.ADMIN, email);
        return new AccessTokenResponse(member.getId(), accessToken);
    }

    private Member createAdmin(String email, String password) {
        String code = UUID.randomUUID().toString();
        Recruitment recruitment = recruitmentRepository.save(new Recruitment(code));
        Member member = new Member(email, password, Role.ADMIN, recruitment);
        return memberRepository.save(member);
    }

    @Transactional
    public AccessTokenResponse loginForApplicant(ApplicantLoginRequest request) {
        String email = request.email();
        String password = request.password();
        Recruitment recruitment = recruitmentRepository.findBySecretCode(request.recruitmentCode())
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));

        Member member = memberRepository.findByEmailAndRecruitment(email, recruitment)
                .orElseGet(() -> createApplicant(email, password, recruitment));
        String accessToken = jwtTokenProvider.createAccessToken(Role.APPLICANT, email);
        return new AccessTokenResponse(member.getId(), accessToken);
    }

    public Member createApplicant(String email, String password, Recruitment recruitment) {
        Member member = new Member(email, password, Role.APPLICANT, recruitment);
        return memberRepository.save(member);
    }
}
