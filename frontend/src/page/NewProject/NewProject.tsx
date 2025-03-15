import React, { useState } from "react";
import { Container, Form, Button, Row, Col } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import apiClient from "../../api/apiClient";

interface FormData {
  name: string;
  // language: string;
  description: string;
  // cpuLimit: string;
  // memoryLimit: string;
  // mode: string;
}

const NewProject = () => {
  const [formData, setFormData] = useState<FormData>({
    name: "",
    // language: "",
    description: "",
    // cpuLimit: "1 vCPU",
    // memoryLimit: "1GB",
    // mode: "normal",
  });

  // const languages = ["C", "Python", "Java", "JavaScript", "Go", "Rust"];
  // const cpuOptions = ["0.5 vCPU", "1 vCPU", "2 vCPU"];
  // const memoryOptions = ["512MB", "1GB", "2GB", "4GB"];

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log("Container Data:", formData);

    apiClient.post("/api/projects", formData, {headers: {
      Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
      'Accept': 'application/json'
    }}).then((response) => {
      console.log("✅ 프로젝트 생성 성공:", response.data);
      alert("✅ 프로젝트가 성공적으로 생성되었습니다!");
    }).catch((error) => {
      console.error("🚨 프로젝트 생성 실패:", error);
      alert("🚨 프로젝트 생성 중 오류가 발생했습니다!");
    });
  };

  return (
    <Container className="mt-4">
      <h2 className="mb-4">컨테이너 생성</h2>
      <Form onSubmit={handleSubmit}>
        <Form.Group className="mb-3">
          <Form.Label>프로젝트 이름</Form.Label>
          <Form.Control type="text" name="name" value={formData.name} onChange={handleChange} required />
        </Form.Group>

        {/* <Form.Group className="mb-3"> */}
          {/* <Form.Label>사용할 언어</Form.Label>
          <Form.Select name="language" value={formData.language} onChange={handleChange} required>
            <option value="">언어 선택...</option>
            {languages.map((lang) => (
              <option key={lang} value={lang}>{lang}</option>
            ))}
          </Form.Select>
        </Form.Group> */}

        <Form.Group className="mb-3">
          <Form.Label>상세 설명</Form.Label>
          <Form.Control as="textarea" name="description" rows={3} value={formData.description} onChange={handleChange} />
        </Form.Group>

        {/* <Row className="mb-3">
          <Col md={6}>
            <Form.Group>
              <Form.Label>CPU 제한</Form.Label>
              <Form.Select name="cpuLimit" value={formData.cpuLimit} onChange={handleChange}>
                {cpuOptions.map((option) => (
                  <option key={option} value={option}>{option}</option>
                ))}
              </Form.Select>
            </Form.Group>
          </Col>
          <Col md={6}>
            <Form.Group>
              <Form.Label>메모리 제한</Form.Label>
              <Form.Select name="memoryLimit" value={formData.memoryLimit} onChange={handleChange}>
                {memoryOptions.map((option) => (
                  <option key={option} value={option}>{option}</option>
                ))}
              </Form.Select>
            </Form.Group>
          </Col>
        </Row> */}

        {/* <Form.Group className="mb-3">
          <Form.Label>실행 모드</Form.Label>
          <div>
            <Form.Check
              type="radio"
              label="일반 모드"
              name="mode"
              value="normal"
              checked={formData.mode === "normal"}
              onChange={handleChange}
            />
            <Form.Check
              type="radio"
              label="디버그 모드"
              name="mode"
              value="debug"
              checked={formData.mode === "debug"}
              onChange={handleChange}
            />
          </div>
        </Form.Group> */}

        <Button variant="primary" type="submit">컨테이너 생성</Button>
      </Form>
    </Container>
  );
};

export default NewProject;
