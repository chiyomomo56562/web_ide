package com.web_ide.repository;

import com.web_ide.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProjectRepository extends JpaRepository<Project , Long> {
    Page<Project> findByUserId(Long userId, Pageable pageable);
}