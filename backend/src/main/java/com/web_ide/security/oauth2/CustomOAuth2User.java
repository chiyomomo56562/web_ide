package com.web_ide.security.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User,UserDetails {

    // 외부 제공자로부터 받은 사용자 정보 (예: Kakao API 응답)
    private final Map<String, Object> attributes;
    // 내부 DB의 사용자 고유 ID (User 엔티티의 id)
    private final Long id;

    public CustomOAuth2User(Map<String, Object> attributes, Long id) {
        this.attributes = attributes;
        this.id = id;
    }
    
    public CustomOAuth2User(Long id) {
        this.attributes = Collections.emptyMap(); // 기본 빈 맵
        this.id = id;
    }

    // 내부 사용자 고유 ID 반환 (JWT 발급 등 후속 처리에 사용)
    public Long getId() {
        return id;
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
    
    public String getEmail() {
    	return "";
    }
    
    public String getNickname() {
    	return ((Map<String, Object>)attributes.get("properties")).get("nickname").toString();
    }
    @Override
    public String toString() {
    	return "{id='"+ 
    				getId().toString() +
    				"', nickname='"+((Map<String, Object>)attributes.get("properties")).get("nickname")+
    				"', email=\"\"," +
    				"}";
    }
    @Override
    public String getUsername() {
    	return getId().toString();
    }

    @Override
    public String getPassword() {
        return null; // OAuth2 로그인 사용자는 비밀번호 없음
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}