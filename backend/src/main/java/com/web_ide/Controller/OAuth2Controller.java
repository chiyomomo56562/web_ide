package com.web_ide.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Controller {
	 private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class); 

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2Controller(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    /**
     * 카카오 로그인 URL을 생성하여 반환
     */
    @GetMapping("/kakao/login")
    public ResponseEntity<Map<String, String>> getKakaoLoginUrl() {
        // 카카오 OAuth2 ClientRegistration 정보 가져오기
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId("kakao");

        // 카카오 인증 URL 생성
        String kakaoAuthUrl = registration.getProviderDetails().getAuthorizationUri() +
                "?client_id=" + registration.getClientId() +
                "&redirect_uri=" + registration.getRedirectUri() +
                "&response_type=code";

        // URL을 JSON 형태로 반환
        Map<String, String> response = new HashMap<>();
        response.put("url", kakaoAuthUrl);
        return ResponseEntity.ok(response);
    }
}