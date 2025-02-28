import React, { useState } from "react";
import { Container, Row, Col, Button } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import Explorer from "../../component/Explorer/Explorer";

const IDE = () =>  {
    const [output, setOutput] = useState<string>("");
  
    const handleRunCode = () => {
      setOutput("실행 결과 출력...\n> 컴파일 완료"); // 실제 실행 로직 추가 필요
    };
  
    return (
      <Container fluid className="mt-2" style={{ height: "100vh" }}>
        <Row className="h-100">
          {/* 좌측 탐색기 */}
          <Explorer />
  
          {/* 코드 편집기 + 터미널 */}
          <Col md={10} className="d-flex flex-column border-end bg-light">
            <div className="p-2 border-bottom bg-secondary text-light">코드 편집기</div>
            <textarea 
              className="flex-grow-1 form-control" 
              style={{ fontFamily: "monospace", height: "70%" }}
              placeholder="코드를 입력하세요..."
            ></textarea>
            
            {/* 터미널을 코드 편집기 아래 배치 */}
            <div className="p-2 border-top bg-secondary text-light">터미널</div>
            <div 
              className="p-2 bg-black text-light" 
              style={{ fontFamily: "monospace", overflowY: "auto", height: "30%" }}
            >
              {output}
            </div>
          </Col>
        </Row>
      </Container>
    );
  };
  

export default IDE;
