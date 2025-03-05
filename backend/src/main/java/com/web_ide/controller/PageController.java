package com.web_ide.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class PageController {
    // 메인 페이지
    @GetMapping("/")
    public String mainPage() {
        return "main";
    }

    // 프로젝트 생성 페이지
    @GetMapping("/new-project")
    public String newProjectPage() {
        return "newProject";
    }

    // 코드 편집 페이지
    @GetMapping("/projects/{projectId}/editor")
    public String editorPage() {
        return "editor";
    }

    // 사용자 프로필 페이지
    @GetMapping("/profile/{userId}")
    public String profilePage() {
        return "profile";
    }

    // 오류 페이지
    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }
}
