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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    public ResponseEntity<List<Object>> getProjects(ProjectParams params) {
        Page<ProjectResponseDto> projects = projectService.getProjects(
                params.getPage(), params.getSorted(), params.getLimit());

        int maxPage = projects.getTotalPages();
        List<ProjectResponseDto> projectList = projects.getContent();
        List<Object> response = new ArrayList<>();
        response.add(maxPage);
        response.add(projectList);

        return ResponseEntity.ok(response);
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
