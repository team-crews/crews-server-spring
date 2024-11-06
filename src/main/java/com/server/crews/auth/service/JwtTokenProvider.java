package com.server.crews.auth.service;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.SignatureAlgorithm.HS384;

import com.server.crews.auth.domain.RefreshToken;
import com.server.crews.auth.domain.Role;
import com.server.crews.global.exception.CrewsErrorCode;
import com.server.crews.global.exception.CrewsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private static final SignatureAlgorithm ACCESS_TOKEN_ALGORITHM = HS256;
    private static final SignatureAlgorithm REFRESH_TOKEN_ALGORITHM = HS384;

    private final Key key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidityInMilliseconds) {
        this.key = decodeSecretKey(secret);
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    private Key decodeSecretKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Role role, String payload) {
        return createToken(role, payload, accessTokenValidityInMilliseconds, ACCESS_TOKEN_ALGORITHM);
    }

    public RefreshToken createRefreshToken(Role role, String payload) {
        String token = createToken(role, payload, refreshTokenValidityInMilliseconds, REFRESH_TOKEN_ALGORITHM);
        return new RefreshToken(payload, refreshTokenValidityInMilliseconds / 1000, token);
    }

    private String createToken(Role role, String payload, long validityInMilliseconds, SignatureAlgorithm algorithm) {
        Claims claims = Jwts.claims().setSubject(payload);
        claims.put("role", role.name());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(validity)
                .signWith(key, algorithm)
                .compact();
    }

    public String getPayload(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Role getRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Role.valueOf((String) claims.get("role"));
    }

    public void validateAccessToken(String token) {
        if (!validateToken(token).equals(ACCESS_TOKEN_ALGORITHM)) {
            throw new CrewsException(CrewsErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    public void validateRefreshToken(String token) {
        if (!validateToken(token).equals(REFRESH_TOKEN_ALGORITHM)) {
            throw new CrewsException(CrewsErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private SignatureAlgorithm validateToken(String token) {
        try {
            String algorithm = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getHeader()
                    .getAlgorithm();
            return SignatureAlgorithm.forName(algorithm);
        } catch (MalformedJwtException e) {
            throw new CrewsException(CrewsErrorCode.MALFORMED_JWT);
        } catch (ExpiredJwtException e) {
            throw new CrewsException(CrewsErrorCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new CrewsException(CrewsErrorCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            throw new CrewsException(CrewsErrorCode.INVALID_JWT);
        }
    }
}
