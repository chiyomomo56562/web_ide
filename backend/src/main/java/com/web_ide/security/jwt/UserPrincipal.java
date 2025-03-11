package com.web_ide.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import com.web_ide.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserPrincipal implements UserDetails{
	private static final long serialVersionUID = 1L;
	
	private final User user;
	public UserPrincipal(User user) {
		this.user = user;
	}
	
	public User getUser() {
        return this.user;
    }
	
	public long getId() {
		//임의 생성 ID
		return user.getId();
	}
	
	public String getNickName() {
		return user.getNickname();
	}
	
	public String getEmail() {
		return user.getEmail();
	}
	
//	role을 사용을 안하는데 작성은 필수라 null로 반환
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(){
		return null;
	}
	
	@Override
    public String getPassword() {
        return user.getPwd();
    }
	
	@Override
    public String getUsername() {
        return user.getLoginId();
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
