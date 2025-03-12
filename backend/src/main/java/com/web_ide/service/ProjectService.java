package com.web_ide.service;

import com.web_ide.dto.ProjectRequestDto;
import com.web_ide.dto.ProjectResponseDto;
import com.web_ide.entity.Folder;
import com.web_ide.entity.Project;
import com.web_ide.entity.User;
import com.web_ide.repository.FolderRepository;
import com.web_ide.repository.ProjectRepository;
import com.web_ide.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    public Page<ProjectResponseDto> getProjects(Long userId, int page, String sorted, int limit) {
        Sort sort;
        if ("oldest".equalsIgnoreCase(sorted)) {
            sort = Sort.by(Sort.Direction.ASC, "updatedAt");
        } else {
            //기본이 내림차순?
            sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        }
        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<Project> projectPage = projectRepository.findByUserId(userId,pageable);
        return projectPage.map(ProjectResponseDto::fromEntity);

    }

    //프로젝트 생성하기
    public ProjectResponseDto createProject(ProjectRequestDto requestDto, Long userId) {
        //유저 조회하기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 유저입니다"));

        //그.. root폴더 생성하기
        Folder rootFolder = new Folder();
        rootFolder.setName(requestDto.getName());//프로젝트명과 root폴더명 통일
        rootFolder.setRelativePath("/");
        rootFolder.setParent(null);
        Folder saveFolder = folderRepository.save(rootFolder);

        //폴더와 유저 연결하기
        Project project = Project.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .user(user)
                .rootFolder(saveFolder)
                .build();
        Project saveProject = projectRepository.save(project);

        //생성된 프로젝트의 정보를 루트 폴더에 반영해주기
        saveFolder.setProject(saveProject);
        folderRepository.save(saveFolder);

        return ProjectResponseDto.fromEntity(saveProject);
    }

    //프로젝트 수정하기
    public ProjectResponseDto updateProject(Long projectid, ProjectRequestDto requestDto, Long userId) {
        Project project = projectRepository.findById(projectid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트명입니다"));

        if(!project.getUser().getId().equals(userId)){
            throw new SecurityException("수정 권한이 없습니다.");
        }

        if (requestDto.getName() != null && !requestDto.getName().isBlank()) {
            project.changeProjectName(requestDto.getName());
        }

        if(requestDto.getDescription() != null && !requestDto.getDescription().isBlank()){
            project.changeProjectDescription(requestDto.getDescription());
        }

        Project updateProject = projectRepository.save(project);
        return ProjectResponseDto.fromEntity(updateProject);
    }

}
