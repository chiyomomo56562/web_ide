package com.web_ide.Controller;

import com.web_ide.dto.JwtAuthenticationResponseDto;
import com.web_ide.dto.LoginRequestDto;
import com.web_ide.security.jwt.JwtTokenProvider;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider tokenProvider;
	
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }
	
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDto loginRequest) {
        // 1. AuthenticationManager를 통해 사용자 인증 진행
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. 인증 성공 시, SecurityContext에 인증 정보를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. JwtTokenProvider를 사용해 Access Token과 Refresh Token 생성
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        // 4. JwtAuthenticationResponse DTO에 토큰 정보를 담아 클라이언트에 반환
        JwtAuthenticationResponseDto response = new JwtAuthenticationResponseDto(accessToken, refreshToken);
        return ResponseEntity.ok(response);
    }
}
