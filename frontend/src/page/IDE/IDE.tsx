import React, { useEffect, useRef } from "react";
import { Container, Row, Col } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import Explorer from "../../component/Explorer/Explorer";
import CodeEditor from "../../component/CodeEditor/CodeEditor";
import { useDispatch, useSelector } from "react-redux";
import { resetContainerInfo, Rootstate, setContainerInfo } from "../../store/store";
import startContainer from "../../api/startContainer";
import { useParams } from "react-router-dom";
import TerminalComponent from "../../component/TerminalComponent/TerminalComponent";

const IDE = () => {
  const {projectId} = useParams();
  const dispatch = useDispatch();
  const containerName = useSelector((state: Rootstate) => state.containerInfo.name);
  const containerUrl = useSelector((state:Rootstate) => state.containerInfo.url);
  const containerState = useSelector((state:Rootstate) => state.containerInfo.state);

  //중복 실행 방지를 위한 플래그
  const hasInitialized = useRef(false);
  useEffect(() => {
    console.log("IDE 페이지 진입!!!!!!!!!!!!!!!!!  ",projectId);
    dispatch(resetContainerInfo());
    // 중복 실행 방지
    if (!projectId || hasInitialized.current) return;
    hasInitialized.current = true;
    /**
     * 컨테이너 생성
     */
    const initializeContainer = async () => {
      console.log("컨테이너 생성 시작  ", projectId);
      const projectIdNum = Number(projectId);
      if (!containerUrl || containerState === "stopped") {
        const containerData = await startContainer(projectIdNum);
        if (containerData) {
          // 컨테이너 생성에 성공했으면 redux-store에 저장
          dispatch(setContainerInfo({
            name: containerData.name,
            state: "running",
            url: containerData.url
          }));
        }
      }
    };

    initializeContainer();
    }, [dispatch, projectId]);
  

  return (
    <Container fluid className="mt-2" style={{ height: "100vh" }}>
      <Row className="h-100 d-flex flex-nowrap gap-0 m-0">
        {/* 좌측 탐색기 */}
        <Col md={3} className="bg-dark text-light border-right p-0" style={{ overflowY: "auto", borderRight: "2px solid #333" }}>
          <Explorer containerName={containerName} />
        </Col>

        {/* 코드 편집기 + 터미널 */}
        <Col md={9} className="d-flex flex-column p-0 m-0">
          <CodeEditor containerName={containerName} />

          {/* 터미널 */}
          <div className="p-2 border-top bg-secondary text-light">터미널</div>
          <div style={{ flexGrow: 1, minHeight: "30%", backgroundColor: "black" }}>
            {containerName && <TerminalComponent containerName={containerName} />}
          </div>
        </Col>
      </Row>
    </Container>
  );
};

export default IDE;
