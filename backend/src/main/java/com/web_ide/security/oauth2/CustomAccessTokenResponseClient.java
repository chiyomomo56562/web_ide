package com.web_ide.security.oauth2;

import org.springframework.http.*;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Logger;

@Component
public class CustomAccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
    private static final Logger logger = Logger.getLogger(CustomAccessTokenResponseClient.class.getName());
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        logger.info("🔹 CustomAccessTokenResponseClient 실행됨"); // ✅ 실행 여부 확인 로그

        String tokenUri = authorizationGrantRequest.getClientRegistration().getProviderDetails().getTokenUri();
        logger.info("🔹 요청된 토큰 URI: " + tokenUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "grant_type=authorization_code"
                + "&client_id=" + authorizationGrantRequest.getClientRegistration().getClientId()
                + "&client_secret=" + authorizationGrantRequest.getClientRegistration().getClientSecret()
                + "&redirect_uri=" + authorizationGrantRequest.getClientRegistration().getRedirectUri()
                + "&code=" + authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode();

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(tokenUri, HttpMethod.POST, requestEntity, String.class);
            logger.info("✅ 카카오 액세스 토큰 요청 성공: " + response.getBody());
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String accessToken = jsonNode.get("access_token").asText();

            return OAuth2AccessTokenResponse.withToken(accessToken) // ✅ 실제 응답 값을 넣어야 함
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    .build(); //CustomOAuth2Userservice가 실행된다.
        } catch (Exception ex) {
            logger.severe("❌ 카카오 액세스 토큰 요청 실패: " + ex.getMessage());
            throw new RuntimeException("카카오 액세스 토큰 요청 실패", ex);
        }
    }
}