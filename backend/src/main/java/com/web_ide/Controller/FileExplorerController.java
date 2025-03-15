package com.web_ide.Controller;

import org.springframework.web.bind.annotation.*;

import com.web_ide.websocket.WebSocketTerminalController;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/files")
public class FileExplorerController {
	private static final Logger logger = Logger.getLogger(WebSocketTerminalController.class.getName());

    @GetMapping("/tree/{containerId}")
    public List<Map<String, Object>> getFileTree(@PathVariable String containerId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        try {
        	logger.info("load directory list");
            // ✅ 도커 컨테이너 내부에서 `/workspace` 디렉토리 목록 가져오기
            ProcessBuilder processBuilder = new ProcessBuilder(
            		"docker", "exec", containerId, "find", "/workspace", "-type", "d", "-printf", "%p/\n", "-o", "-type", "f", "-printf", "%p\n"
//                "docker", "exec", containerId, "find", "/workspace", "-type", "d", "-o", "-type", "f"
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            logger.info("loaddirectory list complete!");

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            logger.info("reader" + reader);
            List<String> paths = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                paths.add(line);
            }

            process.waitFor();

            if (paths.isEmpty()) {
                return Collections.emptyList();
            }

            // ✅ 파일 트리 생성
            tree = buildTree(paths);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tree;
    }

    private List<Map<String, Object>> buildTree(List<String> paths) {
        Map<String, Map<String, Object>> nodes = new HashMap<>();
        List<Map<String, Object>> tree = new ArrayList<>();

        for (String path : paths) {
            logger.info("📁 처리 중: " + path);
            String[] parts = path.split("/");
            StringBuilder fullPath = new StringBuilder();
            Map<String, Object> parent = null;

            boolean isFolder = path.endsWith("/"); // ✅ 폴더 여부 체크

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                logger.info("🔹 현재 part: " + part);
                if (part.isEmpty()) continue;

                fullPath.append("/").append(part);
                String currentPath = fullPath.toString();

                if (!nodes.containsKey(currentPath)) {
                    Map<String, Object> node = new HashMap<>();
                    node.put("id", currentPath);
                    node.put("name", part);
                    
                    if (isFolder) { // ✅ 폴더인 경우만 children 추가
                        node.put("children", new ArrayList<>());
                    }

                    nodes.put(currentPath, node);

                    if (parent == null) {
                        tree.add(node); // ✅ 최상위 폴더 추가
                    } else {
                        // ✅ 부모가 폴더(`children`이 있는 경우)일 때만 추가
                        if (parent.containsKey("children")) {
                            ((List<Map<String, Object>>) parent.get("children")).add(node);
                        }
                    }
                }

                // ✅ 부모가 폴더(`children`이 있는 경우)일 때만 parent 업데이트
                if (nodes.get(currentPath).containsKey("children")) {
                    parent = nodes.get(currentPath);
                }
            }
        }

        logger.info("✅ 최종 트리 데이터: " + tree);
        return tree;
    }
}