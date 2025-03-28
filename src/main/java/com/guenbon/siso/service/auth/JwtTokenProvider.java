package com.guenbon.siso.service.auth;

import com.guenbon.siso.entity.Member;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AuthErrorCode;
import com.guenbon.siso.support.constants.MemberRole;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Slf4j
public class JwtTokenProvider {
    private final String ISSUER = "siso";
    private final String ID = "id";
    private final String ROLE = "role";
    private final String encodedKey;

    private final Long accessTokenValidTime = 1800000L;
    private final Long refreshTokenValidTime = 604800000L;

    public JwtTokenProvider(@Value("${jwt.secret.key}") String secretKey) {
        this.encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT access 토큰 생성
    public String createAccessToken(Member member) {
        // claim : id
        Map<String, Object> claims = new HashMap<>();
        claims.put(ID, member.getId());
        claims.put(ROLE, member.getRole());

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
    public String createRefreshToken() {//
        // 발행시간
        Date now = new Date();
        // refresh 토큰 발행
        return Jwts.builder()
                .setId(UUID.randomUUID().toString()) // 고유한 jti 추가
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS512, encodedKey)
                .setIssuer(ISSUER)
                .compact();
    }

    // 토큰 검증 및 검증에 성공할 경우 claim 값 반환
    public Jws<Claims> verifySignature(String token) {

        if (token == null || token.isBlank()) {
            throw new CustomException(AuthErrorCode.NULL_OR_BLANK_TOKEN);
        }

        try {
            return Jwts.parser().setSigningKey(encodedKey).requireIssuer(ISSUER)
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(AuthErrorCode.EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(AuthErrorCode.UNSUPPORTED);
        } catch (MalformedJwtException e) {
            throw new CustomException(AuthErrorCode.MALFORMED);
        } catch (SignatureException e) {
            throw new CustomException(AuthErrorCode.SIGNATURE);
        }
    }

    public Long getMemberId(String token) {
        return verifySignature(token).getBody().get(ID, Long.class);
    }

    public MemberRole getRole(String token) {
        return MemberRole.from(verifySignature(token).getBody().get(ROLE, String.class));
    }
}
