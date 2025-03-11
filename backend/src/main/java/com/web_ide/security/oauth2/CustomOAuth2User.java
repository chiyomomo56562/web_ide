package com.web_ide.security.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    // 외부 제공자로부터 받은 사용자 정보 (예: Kakao API 응답)
    private final Map<String, Object> attributes;
    // 내부 DB의 사용자 고유 ID (User 엔티티의 id)
    private final Long userId;

    public CustomOAuth2User(Map<String, Object> attributes, Long userId) {
        this.attributes = attributes;
        this.userId = userId;
    }

    // 내부 사용자 고유 ID 반환 (JWT 발급 등 후속 처리에 사용)
    public Long getUserId() {
        return userId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 권한 정보를 필요로 하지 않는 경우 빈 컬렉션 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        // 여기서는 Kakao API 응답의 "id" 값을 문자열로 반환합니다.
        // 고유의 식별자를 반환
        return attributes.get("id").toString();
    }
}