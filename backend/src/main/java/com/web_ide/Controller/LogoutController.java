package com.web_ide.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LogoutController {
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		//refresh 토큰 삭제
		//accessToken은 클라이언트에서 삭제해야한다.
	    Cookie cookie = new Cookie("refreshToken", null);
	    cookie.setHttpOnly(true);
	    cookie.setPath("/");
	    cookie.setMaxAge(0);  // 즉시 삭제
	    response.addCookie(cookie);
	    return ResponseEntity.ok("로그아웃 완료");
	}
}
