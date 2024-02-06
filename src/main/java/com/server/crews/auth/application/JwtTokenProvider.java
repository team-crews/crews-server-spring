package com.server.crews.auth.application;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.SignatureAlgorithm.HS384;

import com.server.crews.auth.domain.Role;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
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
            @Value("${jwt.secret}") final String secret,
            @Value("${jwt.access-token-validity}") final long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-validity}") final long refreshTokenValidityInMilliseconds) {
        this.key = decodeSecretKey(secret);
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    private Key decodeSecretKey(final String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(final Role role, final String payload) {
        return createToken(
                role, payload, accessTokenValidityInMilliseconds, ACCESS_TOKEN_ALGORITHM);
    }

    public String createRefreshToken(final Role role, final String payload) {
        return createToken(
                role, payload, refreshTokenValidityInMilliseconds, REFRESH_TOKEN_ALGORITHM);
    }

    private String createToken(
            final Role role,
            final String payload,
            final long validityInMilliseconds,
            final SignatureAlgorithm algorithm) {
        Claims claims = Jwts.claims().setSubject(payload);
        claims.put("role", role.name());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(validity)
                .signWith(algorithm, key)
                .compact();
    }

    public String getPayload(final String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Role getRole(final String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Role.valueOf((String) claims.get("role"));
    }

    public boolean validateAccessToken(String token) {
        if(!validateToken(token).equals(ACCESS_TOKEN_ALGORITHM)) {
            throw new CrewsException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        return true;
    }

    public boolean validateRefreshToken(String token) {
        if(!validateToken(token).equals(REFRESH_TOKEN_ALGORITHM)) {
            throw new CrewsException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        return true;
    }

    private SignatureAlgorithm validateToken(String token) {
        try {
            String algorithm = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getHeader()
                    .getAlgorithm();
            return SignatureAlgorithm.forName(algorithm);
        } catch (MalformedJwtException e) {
            throw new CrewsException(ErrorCode.MALFORMED_JWT);
        } catch (ExpiredJwtException e) {
            throw new CrewsException(ErrorCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new CrewsException(ErrorCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            throw new CrewsException(ErrorCode.INVALID_JWT);
        }
    }
}