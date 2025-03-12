package com.web_ide.security.jwt;

import com.web_ide.security.oauth2.CustomOAuth2User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;
    
    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;
    
    @Value("${app.jwtRefreshExpirationInMs}")
    private int jwtRefreshExpirationInMs;

    // 서명키를 생성합니다.
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Authentication 객체에서 내부 사용자 ID를 추출합니다.
    private Long extractId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getId();
        } else if (principal instanceof CustomOAuth2User) {
            return ((CustomOAuth2User) principal).getId();
        } else {
            throw new IllegalStateException("알 수 없는 principal 타입: " + principal.getClass().getName());
        }
    }

    /**
     * Access Token 생성
     * 
     * @param authentication 인증 객체
     * @return 생성된 JWT Access Token 문자열
     */
    
    public String generateAccessToken(Authentication authentication, String loginType) {
        Long userId = extractId(authentication); //id(number)를 가져온다.
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .claim("loginType", loginType) //로그인 타입을 지정
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * refresh token 생성
     * @param authentication
     * @return
     */
    public String generateRefreshToken(Authentication authentication, String loginType) {
        Long userId = extractId(authentication);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .claim("loginType", loginType) //로그인 타입을 지정
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * token에서 유저의 id를 뽑아낸다.
     * @param  token
     * @return subject of token (토큰을 생성할 때 설정했었다.)
     */
    public Long getIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
    /**
     * loginType을 반환
     * @param token
     * @return loginType ("local" or "oauth2")
     */
    public String getLoginTypeFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("loginType", String.class); // 🔹 로그인 타입 반환 ("local" 또는 "oauth2")
    }

    /**
     * jwt 토큰의 유효성을 검증
     * 
     * @param token
     * @return 토큰이 유효하면 true
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
//			logger.info("token is not validated: ", ex);
        }
        return false;
    }
}

