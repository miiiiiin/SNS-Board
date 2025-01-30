package com.example.board.service;

import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private static final SecretKey key =  Jwts.SIG.HS256.key().build();

    // 실제 인증에 사용될 public method 생성, 사용자 정보 중 username 전달받아 jwt 토큰 발행
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    // 토큰 검증 시, 토큰으로부터 username 추출하는 형태
    public String getUsername(String accessToken) {
        return getSubject(accessToken);
    }

    private String generateToken(String subject) {
        var now = new Date();
        // 토큰 만료 시점 : 현재 시간 기준 3시간 이후
        var exp = new Date(now.getTime() + (1000 * 60 * 60 * 3));
        return Jwts.builder().subject(subject).signWith(key)
                .issuedAt(now) // 발행 시점
                .expiration(exp) // 만료 시점
                .compact();
    }

    private String getSubject(String token) {
        // jwt 검증 코드
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            logger.error("JwtException", e);
            throw e;
        }
    }
}
