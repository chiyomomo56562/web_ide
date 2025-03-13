import React, { useEffect } from "react";
import { Container, Row, Col } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import Explorer from "../../component/Explorer/Explorer";
import CodeEditor from "../../component/CodeEditor/CodeEditor";
import { useDispatch, useSelector } from "react-redux";
import { Rootstate, setContainerInfo } from "../../store/store";
import startContainer from "../../api/startContainer";

const IDE = ({projectId}) => {
  const dispatch = useDispatch();
  const containerUrl = useSelector((state:Rootstate) => state.containerInfo.url);
  const containerState = useSelector((state:Rootstate) => state.containerInfo.state);
  
  useEffect(() => {
    /**
     * 컨테이너 생성
     */
    const initializeContainer = async () => {
      const projectIdNum = Number(projectId);
      if (!containerUrl || containerState === "stopped") {
        const containerData = await startContainer(projectIdNum);
        if (containerData) {
          // 컨테이너 생성에 성공했으면 redux-store에 저장
          dispatch(setContainerInfo({
            id: containerData.id,
            state: "running",
            url: containerData.url
          }));
        }
      }
    };

    initializeContainer();
    }, [dispatch, projectId, containerUrl, containerState]);
  

  return (
    <Container fluid className="mt-2" style={{ height: "100vh" }}>
      <Row className="h-100 d-flex flex-nowrap gap-0 m-0">
        {/* 좌측 탐색기 */}
        <Col md={3} className="bg-dark text-light border-right p-0" style={{ overflowY: "auto", borderRight: "2px solid #333" }}>
          <Explorer />
        </Col>

        {/* 코드 편집기 + 터미널 */}
        <Col md={9} className="d-flex flex-column p-0 m-0">
          <CodeEditor />

          {/* 터미널 */}
          <div className="p-2 border-top bg-secondary text-light">터미널</div>
          <div 
            className="p-2 bg-black text-light" 
            style={{ fontFamily: "monospace", overflowY: "auto", height: "30%" }}
          >
            {/* {output} */}
          </div>
        </Col>
      </Row>
    </Container>
  );
};

export default IDE;
