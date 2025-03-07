package com.web_ide.Controller;

import com.web_ide.dto.ProjectResponseDto;
import com.web_ide.service.ProjectService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/projects")
    public ResponseEntity<Page<ProjectResponseDto>> getProjects(ProjectParams Params) {
        Page<ProjectResponseDto> projects = projectService.getProjects(
                Params.getPage() , Params.getSorted() , Params.getLimit());
        return ResponseEntity.ok(projects);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class ProjectParams {
        private int page = 0;
        private String sorted = "latest";//기본 정렬
        private int limit = 5;//한번에 보여줄 페이지
    }
}
