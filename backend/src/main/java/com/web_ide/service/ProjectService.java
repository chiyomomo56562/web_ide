package com.web_ide.service;

import com.web_ide.dto.ProjectResponseDto;
import com.web_ide.entity.Project;
import com.web_ide.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public Page<ProjectResponseDto> getProjects(int page, String sorted, int limit) {
        Sort sort;
        if ("oldest".equalsIgnoreCase(sorted)) {
            sort = Sort.by(Sort.Direction.ASC, "updatedAt");
        } else {
            //기본이 내림차순?
            sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        }

        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<Project> projectPage = projectRepository.findAll(pageable);
        return projectPage.map(ProjectResponseDto::fromEntity);
    }
}
