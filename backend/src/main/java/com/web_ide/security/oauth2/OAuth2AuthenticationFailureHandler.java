package com.web_ide.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger logger = Logger.getLogger(OAuth2AuthenticationFailureHandler.class.getName());

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
    	 logger.severe("OAuth2 로그인 실패: " + exception.getMessage());
         logger.severe("실패한 요청 URI: " + request.getRequestURI());

         // 요청된 모든 파라미터를 출력
         request.getParameterMap().forEach((key, value) -> {
             logger.severe("파라미터: " + key + " = " + String.join(",", value));
         });
        
        response.sendRedirect("http://localhost:3000/login?error=" + exception.getMessage());
    }
}