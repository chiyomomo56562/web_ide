package com.web_ide.service;

import com.web_ide.entity.User;
import com.web_ide.repository.UserRepository;
import com.web_ide.security.jwt.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * loginId를 기반으로 사용자를 찾아 반환
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(username);
        if (user == null) { // userName이 아니라, 실제 user 객체가 null인지를 확인해야 합니다.
            throw new UsernameNotFoundException("User not found with loginId: " + username);
        }
        return new UserPrincipal(user);
    }
    
    /**
     * JWT 인증 시, 내부 userId로 사용자를 조회합니다.
     */
    public UserDetails loadUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return new UserPrincipal(user);
    }
}

