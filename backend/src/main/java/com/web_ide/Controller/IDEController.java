package com.web_ide.Controller;

import org.springframework.web.bind.annotation.*;

import com.web_ide.security.oauth2.OAuth2AuthenticationFailureHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.logging.Logger;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class IDEController {
	private static final Logger logger = Logger.getLogger(OAuth2AuthenticationFailureHandler.class.getName());
    @PostMapping("/start-ide")
    public ResponseEntity<Map<String, String>> startIDE(@RequestBody  Map<String, String> request) {
        try {
        	logger.info("IDEController!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        	String projectId = request.get("projectId");
        	logger.info("projectId: "+ projectId);
            String containerName = "ide-" + projectId;
            
         // ✅ 1. 실행 중인 컨테이너가 있으면 기존 URL 반환
            if (isContainerRunning(containerName)) {
                logger.info("✅ 이미 실행 중인 컨테이너: " + containerName);
                String existingPort = getContainerPort(containerName);
                Map<String, String> response = new HashMap<>();
                response.put("name", containerName);
                response.put("url", "http://localhost:" + existingPort);
                logger.info("✅ container start id: " + containerName);
                logger.info("✅ container start url: http://localhost:" + existingPort);
                return ResponseEntity.ok(response);
            }

            // ✅ 2. 실행 중이 아닌 기존 컨테이너가 있으면 삭제
            if (isContainerExisting(containerName)) {
                logger.info("⚠️ 기존 컨테이너 삭제 중: " + containerName);
                removeExistingContainer(containerName);
            }
            // 2.Docker 컨테이너 실행 및 동적 포트 할당
            logger.info("!!!!!!!!!!!!!!!!!!!!!!!containerName: "+ containerName);
            int assignedPort = startDockerContainer(containerName); //여기서 문제 발생!
            logger.info("!!!!!!!!!!!!!!!!!!!!!!!assignedPort: "+ assignedPort);

            // 컨테이너 정보 반환
            Map<String, String> response = new HashMap<>();
            response.put("id", containerName);
            response.put("url", "http://your-server-ip:" + assignedPort);
            logger.info("!!!!!!!!!!!!!!!!!!!!!!!response: "+ response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
        	logger.info("!!!!!!!!!!!!!!!!!!!!!!!error!!!!!!!!!!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // 🔹 사용 가능한 포트 찾기
    private int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    // 🔹 Docker 컨테이너 실행
    private int startDockerContainer(String containerName) throws IOException, InterruptedException {
        int port = findAvailablePort();
        logger.info("!!!!!!!!!!!!!!!!port :"+port);

        ProcessBuilder processBuilder = new ProcessBuilder(
            "docker", "run", "-d",
            "--name", containerName,
            "-p", port + ":8080",
            "ide-image"
        );

        // 🔥 표준 출력 & 에러 출력을 캡처하여 로그로 출력
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            logger.info("Docker 실행 오류 로그: " + line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            logger.info("Docker 컨테이너 실행 실패! Exit code: " + exitCode);
            throw new IOException("Docker 실행 실패. Exit code: " + exitCode);
        }

        return port;
    }

    // 🔹 Nginx 설정 업데이트
//    private void updateNginxConfig(String containerName, int port) throws IOException, InterruptedException {
//        String command = "docker exec nginx sh -c 'sed -i \"/# 개발환경에서 발생하는 오류를 제거해주기 위한 부분/i \\    location /" 
//                + containerName + " {\\n"
//                + "        proxy_pass http://localhost:" + port + ";\\n"
//                + "        proxy_set_header Host $host;\\n"
//                + "        proxy_set_header X-Real-IP $remote_addr;\\n"
//                + "        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;\\n"
//                + "    }\" /etc/nginx/conf.d/default.conf'";
//	    
//	    ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", command);
//	    processBuilder.start().waitFor();
//    }


    // 🔹 Nginx 설정 적용 (reload)
//    private void reloadNginx() throws IOException, InterruptedException {
//        ProcessBuilder processBuilder = new ProcessBuilder("nginx", "-s", "reload");
//        processBuilder.start().waitFor();
//    }
    
    //실행 중인 컨테이너 확인
    private boolean isContainerRunning(String containerName) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("docker", "ps", "--filter", "name=" + containerName, "--format", "{{.Names}}");
        Process process = processBuilder.start();
        process.waitFor();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.readLine() != null; // 컨테이너 이름이 반환되면 실행 중인 것
        }
    }
    //실행 중인 컨테이너의 포트 확인
    private String getContainerPort(String containerName) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("docker", "inspect", "-f", "'{{(index .NetworkSettings.Ports \"8080/tcp\" 0).HostPort}}'", containerName);
        Process process = processBuilder.start();
        process.waitFor();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.readLine().replace("'", "").trim();
        }
    }
    
    // 기존 컨테이너가 존재하는지 확인
    private boolean isContainerExisting(String containerName) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "docker", "ps", "-a", "--filter", "name=" + containerName, "--format", "{{.Names}}"
        );
        Process process = processBuilder.start();
        process.waitFor();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.readLine() != null; // 컨테이너 이름이 반환되면 존재하는 것
        }
    }
    
    // ✅ 기존 컨테이너 삭제 후 정상적으로 삭제되었는지 확인
    private void removeExistingContainer(String containerName) throws IOException, InterruptedException {
        logger.info("⚠️ 기존 컨테이너 삭제 시도: " + containerName);

        ProcessBuilder processBuilder = new ProcessBuilder("docker", "rm", "-f", containerName);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            logger.severe("❌ 기존 컨테이너 삭제 실패: " + containerName);
            throw new IOException("컨테이너 삭제 실패. Exit code: " + exitCode);
        }

        // ✅ 삭제가 정상적으로 되었는지 다시 확인
        if (isContainerExisting(containerName)) {
            logger.severe("❌ 기존 컨테이너가 삭제되지 않음! 다시 시도 필요.");
            throw new IOException("기존 컨테이너 삭제 실패.");
        }

        logger.info("✅ 기존 컨테이너 삭제 완료: " + containerName);
    }
}
