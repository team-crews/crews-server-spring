package com.server.crews.auth.application;

import com.server.crews.auth.domain.Member;
import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.LoginMember;
import com.server.crews.auth.dto.response.AccessTokenResponse;
import com.server.crews.auth.dto.RefreshTokenWithValidity;
import com.server.crews.auth.repository.MemberRepository;
import com.server.crews.auth.repository.RefreshTokenRepository;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    private final int refreshTokenValidityInSecond;

    public AuthService(JwtTokenProvider jwtTokenProvider,
                       RefreshTokenRepository refreshTokenRepository, MemberRepository memberRepository,
                       @Value("${jwt.refresh-token-validity}") int refreshTokenValidityInMilliseconds) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
        this.refreshTokenValidityInSecond = refreshTokenValidityInMilliseconds / 1000;
    }

    @Transactional
    public RefreshTokenWithValidity createRefreshToken(Role role, Long id) {
        String refreshToken = jwtTokenProvider.createRefreshToken(role, String.valueOf(id));
        refreshTokenRepository.deleteByOwnerId(id);
        refreshTokenRepository.save(new RefreshToken(refreshToken, id));
        return new RefreshTokenWithValidity(refreshTokenValidityInSecond, refreshToken);
    }

    public LoginMember findAuthentication(final String accessToken) {
        jwtTokenProvider.validateAccessToken(accessToken);
        String payload = jwtTokenProvider.getPayload(accessToken);
        Member member = memberRepository.findByEmail(payload).orElseThrow(() -> new CrewsException(ErrorCode.MEMBER_NOT_FOUND));
        return new LoginMember(member.getEmail(), member.getRole(), member.getRecruitment().getId());
    }

    public AccessTokenResponse renew(final String refreshToken) {
        jwtTokenProvider.validateRefreshToken(refreshToken);
        refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CrewsException(ErrorCode.INVALID_REFRESH_TOKEN));

        String payload = jwtTokenProvider.getPayload(refreshToken);
        long id = Long.parseLong(payload);
        Role role = jwtTokenProvider.getRole(refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(role, payload);
        return new AccessTokenResponse(id, accessToken);
    }
}
