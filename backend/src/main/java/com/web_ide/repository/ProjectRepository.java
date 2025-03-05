package com.web_ide.repository;

import com.web_ide.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
public interface ProjectRepository extends JpaRepository<Project , Long> {
}
