package com.web_ide.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.web_ide.security.oauth2.OAuth2AuthenticationFailureHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.util.logging.Logger;

@RestController
@RequestMapping("/ide")
public class IDEProxyController {
	private static final Logger logger = Logger.getLogger(OAuth2AuthenticationFailureHandler.class.getName());
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, Integer> containerPortMap = new ConcurrentHashMap<>();

    @GetMapping("/{containerId}/**")
    public void proxyRequest(@PathVariable String containerId, HttpServletRequest request, HttpServletResponse response) throws IOException {
    	logger.info("IDEProxyController!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    	
    	Integer port = containerPortMap.get(containerId);
        if (port == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Container not found");
            return;
        }

        String targetUrl = "http://localhost:" + port + request.getRequestURI();
        response.sendRedirect(targetUrl);
    }

    // 컨테이너 시작 시 포트 저장
    public void registerContainer(String containerId, int port) {
        containerPortMap.put(containerId, port);
    }
}
