package com.web_ide.Controller;

import com.web_ide.dto.ProjectListDto;
import com.web_ide.dto.ProjectRequestDto;
import com.web_ide.dto.ProjectResponseDto;
import com.web_ide.security.oauth2.CustomOAuth2User;
import com.web_ide.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.web_ide.security.jwt.UserPrincipal;
import com.web_ide.service.ProjectService;
import jakarta.validation.Valid;
import lombok.*;

import java.util.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProjectController {
	private static final Logger logger = Logger.getLogger(OAuth2AuthenticationFailureHandler.class.getName());

    private final ProjectService projectService;
    @GetMapping("/projects")
    public ResponseEntity<ProjectListDto> getProjects(@RequestHeader("Authorization") String authorizationHeader, 
    													ProjectParams projectParams) 
    {
    	// "Bearer " 부분 제거하고 순수한 토큰 값만 추출
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = getAuthUserId();
        logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!id:" +userId);
        Page<ProjectResponseDto> projects = projectService.getProjects(
                userId, projectParams.getPage(), projectParams.getSorted(), projectParams.getLimit());
        
        List<ProjectResponseDto> content = (projects.getContent() != null) ? projects.getContent() : new ArrayList<>();
        ProjectListDto projectList = new ProjectListDto(
                projects.getTotalPages(), content);
        logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!projectList: " + projectList);
        return ResponseEntity.ok(projectList);
    }

    @PostMapping("/projects")
    public ResponseEntity<ProjectResponseDto> createProject(@Valid @RequestBody ProjectRequestDto projectRequestDto) {
        Long userId = getAuthUserId();
        ProjectResponseDto responseDto = projectService.createProject(projectRequestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/projects/{projectId}")
    public ResponseEntity<ProjectResponseDto> updateProject(@PathVariable Long projectId,
                                                            @Valid @RequestBody ProjectRequestDto requestDto) {
        Long userId = getAuthUserId();
        ProjectResponseDto responseDto = projectService.updateProject(projectId, requestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    // 로그인된 user id가 없으면 401오류를 던진다
    private Long getAuthUserId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        return userId;
    }

    //로그인 되어있는 user id 가져오기
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal) {
                return ((UserPrincipal) principal).getId();
            } else if (principal instanceof CustomOAuth2User) {
                return ((CustomOAuth2User) principal).getId();
            }
        }
        return null;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProjectParams {
        private int page = 0;
        private String sorted = "latest";//기본 정렬
        private int limit = 5;//한번에 보여줄 페이지
    }
}
