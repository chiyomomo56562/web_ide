package com.web_ide.security.jwt;

import com.web_ide.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
                                    throws ServletException, IOException {
        try {
            // 요청 헤더에서 JWT 토큰 추출 ("Bearer " 접두사 제거)
            String jwt = getJwtFromRequest(request);
            
            // 토큰이 존재하고 유효하면
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // 토큰에서 내부 사용자 ID를 추출
                Long userId = tokenProvider.getUserIdFromJWT(jwt);
                // 해당 userId를 통해 UserDetails 로드 (일반 로그인은 UserPrincipal, OAuth2는 CustomOAuth2User)
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                
                // 인증 객체 생성 (비밀번호는 null, 권한은 userDetails에서 가져옴)
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                // 요청 정보를 추가하여 인증 객체에 부가 정보 설정
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // SecurityContext에 인증 정보를 설정해 이후의 요청에서 사용 가능하도록 함
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        // 필터 체인의 다음 단계로 요청을 전달
        filterChain.doFilter(request, response);
    }
    
	/**
	 * HTTP 요청 헤더에서 "Bearer " 접두사를 제거한 JWT 토큰을 추출
	 * @param request
	 * @return 토큰 문자열, 없으면 null
	 */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
