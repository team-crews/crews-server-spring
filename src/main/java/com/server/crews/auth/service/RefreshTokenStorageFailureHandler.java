package com.server.crews.auth.service;

import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.auth.dto.response.TokenResponse;
import com.server.crews.global.CustomLogger;
import com.server.crews.global.exception.InternalErrorOccurredEvent;
import io.lettuce.core.RedisCommandTimeoutException;
import io.lettuce.core.RedisException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.stereotype.Service;

/*
    Decorator 패턴을 활용해서 RefreshTokenService에서 저장소 관련 예외가 발생한 경우를 처리한다.
 */
@Service
public class RefreshTokenStorageFailureHandler implements RefreshTokenService {
    private static final CustomLogger customLogger = new CustomLogger(RefreshTokenStorageFailureHandler.class);

    private final RefreshTokenService refreshTokenService;
    private final ApplicationEventPublisher eventPublisher;

    public RefreshTokenStorageFailureHandler(
            @Qualifier("refreshTokenManager") RefreshTokenService refreshTokenService,
            ApplicationEventPublisher applicationEventPublisher) {
        this.refreshTokenService = refreshTokenService;
        this.eventPublisher = applicationEventPublisher;
    }

    @Override
    public RefreshToken createRefreshToken(Role role, String username) {
        try {
            return refreshTokenService.createRefreshToken(role, username);
        } catch (RedisConnectionFailureException | RedisSystemException | RedisException e) {
            customLogger.error(e);
            eventPublisher.publishEvent(new InternalErrorOccurredEvent(e, null));
            return new RefreshToken("", 0l, "");
        }
    }

    /*
    500 에러를 그대로 반환한다.
     */
    @Override
    public TokenResponse renew(String refreshToken) {
        return refreshTokenService.renew(refreshToken);
    }

    @Override
    public void delete(String username) {
        try {
            refreshTokenService.delete(username);
        } catch (RedisConnectionFailureException | RedisCommandTimeoutException | RedisSystemException e) {
            customLogger.error(e);
            eventPublisher.publishEvent(new InternalErrorOccurredEvent(e, null));
        }
    }
}
