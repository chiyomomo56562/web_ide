//package com.web_ide.Controller;
//
//import com.web_ide.dto.JwtAuthenticationResponseDto;
//import com.web_ide.security.jwt.JwtTokenProvider;
//import com.web_ide.service.CustomUserDetailsService;
//import com.web_ide.dto.TokenRefreshRequestDto;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.HttpStatus;
//import org.springframework.util.StringUtils;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Collections;
//
//@RestController
//@RequestMapping("/api/auth")
//public class TokenController {
//	private JwtTokenProvider tokenProvider;
//	private CustomUserDetailsService customUserDetailsService;
//	
//	public TokenController(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
//		this.tokenProvider = tokenProvider;
//		this.customUserDetailsService = customUserDetailsService;
//	}
//	
//	@PostMapping("/refresh")
//	public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequestDto tokenRefreshRequestDto){
//		String refreshToken = tokenRefreshRequestDto.getRefreshToken();
//		
//        // Refresh Token이 존재하고 유효한지 확인
//        if (!StringUtils.hasText(refreshToken) || !tokenProvider.validateToken(refreshToken)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("유효하지 않은 Refresh Token입니다.");
//        }
//        
//        // Refresh Token에서 사용자 ID를 추출
//        Long userId = tokenProvider.getUserIdFromJWT(refreshToken);
//        
//        // 사용자 ID로 사용자 정보를 조회
//        UserDetails userDetails = customUserDetailsService.loadUserById(userId);
//
//        // 사용자 정보를 기반으로 인증 객체 생성, 권한은 사용안해서 빈 배열로
//        Authentication authentication =
//                new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
//        // 새 Access Token을 생성
//        String newAccessToken = tokenProvider.generateAccessToken(authentication);
//
//        // 새 Access Token과 기존 Refresh Token을 응답 DTO에 담아 반환
//        JwtAuthenticationResponseDto response =
//                new JwtAuthenticationResponseDto(newAccessToken, refreshToken);
//        return ResponseEntity.ok(response);
//	}
//}
