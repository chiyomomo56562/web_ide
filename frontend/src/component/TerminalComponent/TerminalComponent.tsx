import React, { useEffect, useRef, useState } from "react";
import { Terminal } from "@xterm/xterm";
import "@xterm/xterm/css/xterm.css";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const TerminalComponent = ({ containerName }: {containerName:string} ) => {
    const terminalRef = useRef<HTMLDivElement>(null);
    const stompClientRef = useRef<Client | null>(null);
    const [messages, setMessages] = useState<string[]>([]);
    const inputBufferRef = useRef(""); // 입력 버퍼

    useEffect(() => {
        console.log("here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        if (!terminalRef.current || !containerName) return;  // ✅ ref가 없는 경우 방지
        const term = new Terminal({
            cursorBlink: true,
            fontSize: 14,
            theme: {
                background: "#000000",
                foreground: "#ffffff",
            }
        });

        term.open(terminalRef.current);
        console.log("terminalRef.current: ", terminalRef.current);
        console.log("containerName: ", containerName);
        // ✅ WebSocket 연결
        const socket = new SockJS(`http://localhost:3000/ws`, null,{
            transports: ['websocket'],
        });
        // 특정 주소가 아니라 컨테이너 자체에 접속하는 것
        // websocketConfig에 설정한 엔드포인트를 입력해줘야함
        console.log("socket: ", socket);
        const stompClient = new Client({
            webSocketFactory: () => socket,
            debug: (msg) => console.log("[STOMP DEBUG]", msg),
            onConnect: () => {
                console.log("✅ STOMP 연결 성공!");

                // ✅ 터미널 출력 구독
                stompClient.subscribe(`/topic/terminal/${containerName}`, (message) => {
                    setMessages((prev) => [...prev, message.body]);
                    term.write(message.body + "\r\n");
                });
            },
            onStompError: (frame) => console.error("❌ STOMP 오류:", frame),
        });
        
        stompClient.activate();
        stompClientRef.current = stompClient;

        // ✅ 터미널 입력 이벤트 처리
        term.onData((data) => {
            if(data === "\r"){
                stompClient.publish({
                    destination: `/app/terminal/${containerName}`,
                    body: inputBufferRef.current,
                });

                inputBufferRef.current = "";
                term.write("\r\n"); // ✅ 커서 위치 업데이트
            }
            else if(data === "\x7f"){ // 백스페이스
                if (inputBufferRef.current.length > 0) {
                    inputBufferRef.current = inputBufferRef.current.slice(0, -1); // 버퍼에서 마지막 문자 제거
                    term.write("\b \b"); // 터미널에서 마지막 문자 삭제
                }
            }
            else{
                term.write(data);
                inputBufferRef.current += data;
            }
        });

        return () => {
            stompClient.deactivate();
            term.dispose();
        };
    }, [containerName]);

    return <div ref={terminalRef} style={{ width: "100%", height: "400px", backgroundColor: "black" }} />;
};

export default TerminalComponent;