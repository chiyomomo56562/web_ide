package com.web_ide.Controller;

import com.web_ide.dto.JwtAuthenticationResponseDto;
import com.web_ide.security.jwt.JwtTokenProvider;
import com.web_ide.service.CustomUserDetailsService;
import com.web_ide.security.oauth2.CustomOAuth2User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@RestController
@RequestMapping("/api/auth")
public class RefreshTokenController {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    
    public RefreshTokenController(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 refreshToken 값을 추출합니다.
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || !tokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("유효하지 않은 Refresh Token입니다.");
        }

        // refreshToken에서 사용자 ID 추출
        Long id = tokenProvider.getIdFromJWT(refreshToken);
        String loginType = tokenProvider.getLoginTypeFromJWT(refreshToken);
        
        // 임시 Authentication 객체 생성
        Authentication authentication;
        if("oauth2".equals(loginType)) {
        	authentication = new OAuth2AuthenticationToken(new CustomOAuth2User(id), Collections.emptyList(), "oauth2");
        } else {
//        	serurity context에서 UserDetails를 꺼내온다.
        	UserDetails userDetails = customUserDetailsService.loadUserById(id);
            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }
        
        // 새로운 Token 생성
        String newAccessToken = tokenProvider.generateAccessToken(authentication, loginType);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication, loginType);
        

        // 7. 새로 생성한 Refresh Token을 httpOnly 쿠키에 업데이트 (예: 만료시간 3600초)
        Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/"); // 쿠키가 모든 경로에서 사용되도록 설정
        // 만료 시간을 3600초(1시간)로 설정 (tokenProvider에서 값을 가져와야......)
        refreshCookie.setMaxAge(3600);
        response.addCookie(refreshCookie);
        
        JwtAuthenticationResponseDto responseDto =
                new JwtAuthenticationResponseDto(newAccessToken, newRefreshToken);
        return ResponseEntity.ok(responseDto);
    }
}