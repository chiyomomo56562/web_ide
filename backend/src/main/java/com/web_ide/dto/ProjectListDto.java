package com.web_ide.dto;

import java.util.List;

public class ProjectListDto {
    private int maxPage;
    private List<ProjectResponseDto> projects;

    public ProjectListDto(int maxPage, List<ProjectResponseDto> projects) {
        this.maxPage = maxPage;
        this.projects = projects;
    }
}
