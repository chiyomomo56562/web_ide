package com.web_ide.security.oauth2;

import com.web_ide.dto.LoginResponseDto;
import com.web_ide.dto.UserResponseDto;
import com.web_ide.security.jwt.JwtTokenProvider;
import com.web_ide.security.jwt.UserPrincipal;
import com.web_ide.security.oauth2.CustomOAuth2User;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
import java.util.logging.Logger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private static final Logger logger = Logger.getLogger(OAuth2AuthenticationFailureHandler.class.getName());
	private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtTokenProvider tokenProvider;

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                          HttpServletResponse response,
                                          Authentication authentication) throws IOException, ServletException {
    	logger.severe("in successHandler!!!!!!");
    	SecurityContextHolder.getContext().setAuthentication(authentication);
    	
    	String accessToken = tokenProvider.generateAccessToken(authentication, "oauth2");
        String refreshToken = tokenProvider.generateRefreshToken(authentication, "oauth2");

        // 2) RefreshToken -> HttpOnly 쿠키에 저장
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");  // 필요한 옵션들 (secure, sameSite 등) 설정
        refreshCookie.setMaxAge(60*60); //1시간
        response.addCookie(refreshCookie);

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        
     // 🔹 URL 인코딩 (한글 문제 방지)
        String encodedId = URLEncoder.encode(oAuth2User.getId().toString(), StandardCharsets.UTF_8);
        String encodedNickname = URLEncoder.encode(oAuth2User.getNickname(), StandardCharsets.UTF_8);
        String encodedEmail = URLEncoder.encode(oAuth2User.getEmail(), StandardCharsets.UTF_8);

        // 🔹 React로 리다이렉트 (한글 깨짐 방지)
        String redirectUrl = "http://localhost:3000/auth-redirect"
                + "?id=" + encodedId
                + "&nickname=" + encodedNickname
                + "&email=" + encodedEmail
                + "&accessToken=" + accessToken;
//      userResponseDto생성
        logger.info("✅ OAuth2 로그인 성공 - 응답 완료");
        response.sendRedirect(redirectUrl);
    }
}
