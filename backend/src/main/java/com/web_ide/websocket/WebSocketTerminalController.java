package com.web_ide.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.DestinationVariable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

@Controller
public class WebSocketTerminalController {
    private static final Logger logger = Logger.getLogger(WebSocketTerminalController.class.getName());

    // 실행 중인 터미널 프로세스를 저장할 맵
    private final ConcurrentMap<String, Process> processMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, BufferedWriter> writerMap = new ConcurrentHashMap<>();

    // ✅ 클라이언트가 "/app/terminal/{containerId}" 경로로 메시지를 보낼 때 실행됨
    @MessageMapping("/terminal/{containerId}")
    @SendTo("/topic/terminal/{containerId}") // ✅ 해당 컨테이너를 구독하는 클라이언트에게 전달
    public String handleTerminalInput(@DestinationVariable String containerId, String message) {
        logger.info("📩 받은 메시지: " + message);
        

        if (!writerMap.containsKey(containerId) || writerMap.get(containerId) == null) {
            logger.warning("⚠️ writer가 null입니다. 터미널 프로세스를 다시 시작합니다.");
            startTerminalProcess(containerId);
        }
        
        try {
            BufferedWriter writer = writerMap.get(containerId);
            logger.info("writer: "+ writer);
            if (writer != null) {
                writer.write(message + "\n");
                writer.flush();
                logger.info("✅ 명령어 실행 성공: " + message);
            }
            else {
            	 logger.severe("🚨 writer가 여전히 null입니다. 프로세스가 정상적으로 실행되지 않았을 가능성이 높습니다.");
            }
        } catch (IOException e) {
        	logger.severe("🚨 writer.write() 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        return message; // 클라이언트로 전달
    }

    // ✅ 클라이언트가 특정 컨테이너의 터미널을 구독하면 실행됨
    @SubscribeMapping("/topic/terminal/{containerId}")
    public void startTerminalProcess(@DestinationVariable String containerId) {
    	logger.info("✅ 터미널 프로세스 시작: " + containerId);
        if (processMap.containsKey(containerId)) return; // 이미 실행 중이면 무시
        logger.info("after return");
        try {
        	logger.info("before processbuilder");
            // ✅ Docker 컨테이너 내부에서 `bash` 실행
            ProcessBuilder processBuilder = new ProcessBuilder(
            		//작업 공간 고정하자
                    "docker", "exec", "-i", containerId, "bash", "-c", "cd /workspace && exec bash"
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));

            processMap.put(containerId, process);
            writerMap.put(containerId, writer);

            logger.info("✅ 터미널 프로세스 실행 완료: " + containerId);
            
            // ✅ 새로운 스레드에서 Docker의 터미널 출력을 WebSocket을 통해 클라이언트로 전달
            new Thread(() -> {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.info("📩 Docker 출력: " + line);
                        // 클라이언트에게 메시지 브로드캐스트
                        WebSocketMessageSender.sendMessage("/topic/terminal/" + containerId, line);
                    }
                } catch (IOException e) {
                	logger.severe("🚨 Docker 출력 스트림 오류: " + e.getMessage());
                }
            }).start();

        } catch (IOException e) {
        	logger.severe("🚨 Docker 출력 스트림 오류: " + e.getMessage());
        }
    }

    // ✅ 프로세스 종료 (필요한 경우)
    public void stopTerminalProcess(String containerId) {
        Process process = processMap.get(containerId);
        if (process != null) {
            process.destroy();
            processMap.remove(containerId);
            writerMap.remove(containerId);
        }
    }
}
