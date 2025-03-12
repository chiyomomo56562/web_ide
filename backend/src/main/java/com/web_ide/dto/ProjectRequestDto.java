package com.web_ide.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDto {
    @NotBlank(message = "프로젝트 이름을 입력해주세요")
    private String name;

    private String description;
}
