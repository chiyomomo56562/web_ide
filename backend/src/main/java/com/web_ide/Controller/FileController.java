package com.web_ide.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private static final Logger logger = Logger.getLogger(FileController.class.getName());

    @PostMapping("/save")
    public ResponseEntity<String> saveFile(@RequestBody Map<String, String> request) {
    	logger.info("/api/files/save !!!!!!!!!!!!!!");
        String containerName = request.get("containerName");
        String filePath = request.get("filePath");
        String content = request.get("content");
        logger.info("containerName: "+containerName);
        logger.info("filePath: "+filePath);
        logger.info("content: "+content);
        if (containerName == null || filePath == null || content == null) {
            return ResponseEntity.badRequest().body("❌ 잘못된 요청 데이터");
        }

        try {
            // 🔥 도커 컨테이너 내부에 파일 저장 (echo로 내용 쓰기)
            String command = String.format("docker exec %s sh -c 'echo \"%s\" > %s'", 
            		containerName, content.replace("\"", "\\\""), filePath);

            ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", command);
            Process process = processBuilder.start();
            process.waitFor();

            logger.info("✅ 파일 저장 완료: " + filePath);
            return ResponseEntity.ok("✅ 저장 성공: " + filePath);

        } catch (Exception e) {
            logger.severe("🚨 파일 저장 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("🚨 저장 실패");
        }
    }
    
    @GetMapping("/content/{containerId}")
    public ResponseEntity<Map<String, String>> getFileContent(
        @PathVariable String containerId, 
        @RequestParam String filePath) {

        try {
            logger.info("📂 파일 읽기 요청: " + filePath);
            
            // ✅ 도커 컨테이너 내부에서 파일 읽기
            ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "exec", containerId, "cat", filePath
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            process.waitFor();

            if (content.toString().trim().isEmpty()) {
            	return ResponseEntity.ok(Map.of("content", "// New File\n"));
            }

            logger.info("✅ 파일 읽기 완료: " + filePath);
            return ResponseEntity.ok(Map.of("content", content.toString()));

        } catch (Exception e) {
            logger.severe("🚨 파일 읽기 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "파일을 읽을 수 없습니다."));
        }
    }
}