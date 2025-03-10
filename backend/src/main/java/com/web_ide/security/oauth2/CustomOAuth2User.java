package com.web_ide.security.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final Map<String, Object> attributes;
    private final Long userId;

    public CustomOAuth2User(Map<String, Object> attributes, Long userId) {
        this.attributes = attributes;
        this.userId = userId;
    }

    // 내부 DB의 사용자 고유 ID 반환 (JWT 토큰 발급 등 후속 처리에 사용)
    public Long getUserId() {
        return userId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 필요하지 않으면 빈 권한 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        // Kakao API 응답의 "id" 값을 문자열로 반환 (또는 다른 속성을 선택할 수 있음)
//    	getName은 고유의 식별자를 반환하는 메소드
        return attributes.get("id").toString();
    }
}