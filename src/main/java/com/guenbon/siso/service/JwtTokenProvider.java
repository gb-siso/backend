package com.guenbon.siso.service;

import com.guenbon.siso.exception.UnAuthorizedException;
import com.guenbon.siso.exception.errorCode.AuthErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class JwtTokenProvider {
    private final String ISSUER = "siso";
    private final String ID = "id";
    private String encodedKey;

    private final Long accessTokenValidTime = 1800000L;
    private final Long refreshTokenValidTime = 604800000L;

    public JwtTokenProvider(@Value("${jwt.secret.key}") String secretKey) {
        this.encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT access 토큰 생성
    public String createAccessToken(Long memberId) {
        // claim : id
        Map<String, Object> claims = new HashMap<>();
        claims.put(ID, memberId);

        // 발행시간
        Date now = new Date();

        // access 토큰 발행
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .setIssuer(ISSUER)
                .signWith(SignatureAlgorithm.HS512, encodedKey)
                .compact();
    }

    // JWT refresh 토큰 생성 (access token 재발행용)
    public String createRefreshToken(Long memberId) {
        // claim : id
        Map<String, Object> claims = new HashMap<>();
        claims.put(ID, memberId);

        // 발행시간
        Date now = new Date();

        // refresh 토큰 발행
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS512, encodedKey)
                .setIssuer(ISSUER)
                .compact();
    }

    // 토큰 검증 및 검증에 성공할 경우 claim 값 반환
    public Jws<Claims> verifySignature(String token) {

        if (token == null || token.isBlank()) {
            throw new UnAuthorizedException(AuthErrorCode.NULL_OR_BLANK_TOKEN);
        }

        try {
            return Jwts.parser().setSigningKey(encodedKey).requireIssuer(ISSUER)
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new UnAuthorizedException(AuthErrorCode.EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.info(e.getMessage());
            throw new UnAuthorizedException(AuthErrorCode.UNSUPPORTED);
        } catch (MalformedJwtException e) {
            throw new UnAuthorizedException(AuthErrorCode.MALFORMED);
        } catch (SignatureException e) {
            throw new UnAuthorizedException(AuthErrorCode.SIGNATURE);
        }
    }

    public Long getMemberId(String token) {
        return verifySignature(token).getBody().get(ID, Long.class);
    }
}
