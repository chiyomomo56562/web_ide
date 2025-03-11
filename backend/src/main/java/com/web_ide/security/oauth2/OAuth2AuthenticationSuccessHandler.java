package com.web_ide.security.oauth2;

import com.web_ide.security.jwt.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                          HttpServletResponse response,
                                          Authentication authentication) throws IOException, ServletException {
        // 1. 인증 성공 시, 전달받은 Authentication 객체를 기반으로 JWT 토큰 생성
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        // 2. 클라이언트에 JWT 토큰을 JSON 형식으로 전달합니다.
        response.setContentType("application/json");
        String jsonResponse = "{\"accessToken\": \"" + accessToken + "\", \"refreshToken\": \"" + refreshToken + "\"}";
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        // 리다이렉션
        setDefaultTargetUrl("/");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
