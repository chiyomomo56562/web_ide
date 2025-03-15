import { useState } from "react";
import { Form, Button, Container, Alert, InputGroup } from "react-bootstrap";
import apiClient from "../../api/apiClient";

const Signup = () => {
  // 상태 관리
  const [form, setForm] = useState({
    loginId: "",
    pwd: "",
    nickname: "",
    email: "",
  });
  const [error, setError] = useState("");
  const [idAvailable, setIdAvailable] = useState<boolean|null>(null);

  // 입력값 변경 핸들러
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  // 아이디 중복 확인
  const checkIdAvailability = () => {
    apiClient
      .get(`/api/auth/check-id?id=${form.loginId}`)
      .then((res) => {
        setIdAvailable(res.data.available);
      })
      .catch((err) => {
        setIdAvailable(false);
        setError("아이디 중복 확인 중 오류 발생");
      });
  };

  // 회원가입 요청
  const handleSignup = (e: React.FormEvent) => {
    e.preventDefault();

    apiClient
      .post("/api/auth/signup", form)
      .then((res) => {
        alert("회원가입 성공!");
        window.location.href = "/login"; // 로그인 페이지로 이동
      })
      .catch((err) => {
        setError("회원가입 실패: " + (err.response?.data?.message || "알 수 없는 오류"));
      });
  };

  return (
    <Container className="mt-5" style={{ maxWidth: "400px" }}>
      <h2 className="text-center">회원가입</h2>
      <Form onSubmit={handleSignup}>
        {/* ID 입력 + 중복 확인 */}
        <Form.Group className="mb-3">
          <Form.Label>아이디</Form.Label>
          <InputGroup>
            <Form.Control type="text" name="loginId" placeholder="아이디 입력" value={form.loginId} onChange={handleChange} />
            <Button variant="secondary" onClick={checkIdAvailability}>중복 확인</Button>
          </InputGroup>
          {idAvailable === true && <Alert variant="success">사용 가능한 ID입니다.</Alert>}
          {idAvailable === false && <Alert variant="danger">{error}</Alert>}
        </Form.Group>

        {/* 비밀번호 입력 */}
        <Form.Group className="mb-3">
          <Form.Label>비밀번호</Form.Label>
          <Form.Control type="password" name="pwd" placeholder="비밀번호 입력" value={form.pwd} onChange={handleChange} />
        </Form.Group>

        {/* 닉네임 입력 */}
        <Form.Group className="mb-3">
          <Form.Label>닉네임</Form.Label>
          <Form.Control type="text" name="nickname" placeholder="닉네임 입력" value={form.nickname} onChange={handleChange} />
        </Form.Group>

        {/* 이메일 입력 */}
        <Form.Group className="mb-3">
          <Form.Label>이메일</Form.Label>
          <Form.Control type="email" name="email" placeholder="이메일 입력" value={form.email} onChange={handleChange} />
        </Form.Group>

        {/* 에러 메시지 */}
        {error && <Alert variant="danger">{error}</Alert>}

        {/* 회원가입 버튼 */}
        <Button variant="primary" type="submit" className="w-100">회원가입</Button>
      </Form>
    </Container>
  );
};

export default Signup;