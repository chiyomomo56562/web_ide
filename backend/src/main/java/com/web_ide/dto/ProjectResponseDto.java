package com.web_ide.dto;

import com.web_ide.entity.Project;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponseDto {
    private Long id;
    private String name;
    private Long userId;
    private String userNickname;
    private LocalDateTime updatedAt;


    public static ProjectResponseDto fromEntity(Project project) {
        return ProjectResponseDto.builder()
                .id(project.getId())
                .name(project.getName())
                .userId(project.getUser().getId())
                .userNickname(project.getUser().getNickname())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
