package com.web_ide.Controller;

import com.web_ide.dto.ProjectRequestDto;
import com.web_ide.dto.ProjectResponseDto;
import com.web_ide.security.CustomOAuth2User;
import com.web_ide.security.UserPrincipal;
import com.web_ide.service.ProjectService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    @GetMapping("/projects")
    public ResponseEntity<List<Object>> getProjects(ProjectParams projectParams){
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        Page<ProjectResponseDto> projects = projectService.getProjects(userId, projectParams.getPage(), projectParams.getSorted(), projectParams.getLimit());
        int maxPage = projects.getTotalPages();
        List<ProjectResponseDto> projectList = projects.getContent();
        List<Object> response = new ArrayList<>();
        response.add(maxPage);
        response.add(projectList);

        return ResponseEntity.ok(response);
    }


    //프로젝트 만들기
    @PostMapping("/projects")
    public ResponseEntity<ProjectResponseDto> createProject(@Valid @RequestBody ProjectRequestDto projectRequestDto){
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        ProjectResponseDto responseDto = projectService.createProject(projectRequestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    //프로젝트 수정하기
    @PatchMapping("/projects/{projectId}")
    public ResponseEntity<ProjectResponseDto> updateProject(@PathVariable Long projectId,
                                                            @Valid @RequestBody ProjectRequestDto requestDto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        ProjectResponseDto responseDto = projectService.updateProject(projectId, requestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    //로그인 되어있는 user id 가져오기
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal) {
                return ((UserPrincipal) principal).getId();
            } else if (principal instanceof CustomOAuth2User) {
                return ((CustomOAuth2User) principal).getUserId();
            }
        }
        return null;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public class ProjectParams {
        private int page = 0;
        private String sorted = "latest";//기본 정렬
        private int limit = 5;//한번에 보여줄 페이지
    }

    @GetMapping("/")
    public ResponseEntity comment() {
        return ResponseEntity.ok("Hello World");
    }
}
