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
	
	private UserRepository userRepository;
	
	public CustomUserDetailsService(UserRepository userRepository){
		this.userRepository = userRepository;
	}
	
	/**
	 * loginId를 기반으로 유저를 찾아 반환
	 */
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException{
		User user = userRepository.findByLoginId(userName);
		
		if (userName == null) {
			throw new UsernameNotFoundException("username not found"+ userName);
		}
		return new UserPrincipal(user);
	}
	
	public UserDetails loadUserById(long userId){
//		반환 타입이 Optional<User>이라 .orElseThrow()를 사용 가능하다.
		User user = userRepository.findById(userId)
			    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

		return new UserPrincipal(user);
	}
}
