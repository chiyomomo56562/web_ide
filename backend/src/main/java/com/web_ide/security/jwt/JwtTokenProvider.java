package com.web_ide.security.jwt;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
	@Value("${app.jstSecret}")
	private String jwtSecret;
	
	@Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Value("${app.jwtRefreshExpirationInMs}")
    private int jwtRefreshExpirationInMs;
    
//    서명키 생성
    private Key getSigningKey() {
    	return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * Access Token 생성
     * 
     * @param authentication 인증 객체
     * @return 생성된 JWT Access Token 문자열
     */
    
    public String generateAccessToken(Authentication authentication) {
    	UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    	
//    	만료시간 설정
    	Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        
        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
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
    public String generateRefreshToken(Authentication authentication) {
    	UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    	
    	Date now = new Date();
    	Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationInMs);
        		
		return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * token에서 유저의 id를 뽑아낸다.
     * @param Access token
     * @return subject of token (토큰을 생성할 때 설정했었다.)
     */
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
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
            //ex를 로그로 보여주고 싶은데
        }
        return false;
    }
}
