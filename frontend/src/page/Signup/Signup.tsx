import React from 'react'
import { Form, Button, Container, Alert, InputGroup } from "react-bootstrap";

const Signup = () => {
  return (
    <Container className="mt-5" style={{ maxWidth: "400px" }}>
      <h2 className="text-center">회원가입</h2>
      <Form >
        {/* ID 입력 + 중복 확인 */}
        <Form.Group className="mb-3">
          <Form.Label>아이디</Form.Label>
          <InputGroup>
            <Form.Control
              type="text"
              name="id"
              placeholder="아이디 입력"
            //   value={form.id}
            //   onChange={handleChange}
            />
            <Button variant="secondary" >
              중복 확인
            </Button>
          </InputGroup>
          {/* {idAvailable === true && <Alert variant="success">사용 가능한 ID입니다.</Alert>}
          {idAvailable === false && <Alert variant="danger">{error}</Alert>} */}
        </Form.Group>

        {/* 비밀번호 입력 */}
        <Form.Group className="mb-3">
          <Form.Label>비밀번호</Form.Label>
          <Form.Control
            type="password"
            name="pwd"
            placeholder="비밀번호 입력"
            // value={form.pwd}
            // onChange={handleChange}
          />
        </Form.Group>

        {/* 닉네임 입력 */}
        <Form.Group className="mb-3">
          <Form.Label>닉네임</Form.Label>
          <Form.Control
            type="text"
            name="nickname"
            placeholder="닉네임 입력"
            // value={form.nickname}
            // onChange={handleChange}
          />
        </Form.Group>

        {/* 이메일 입력 */}
        <Form.Group className="mb-3">
          <Form.Label>이메일</Form.Label>
          <Form.Control
            type="email"
            name="email"
            placeholder="이메일 입력"
            // value={form.email}
            // onChange={handleChange}
          />
        </Form.Group>

        {/* 에러 메시지 */}
        {/* {error && <Alert variant="danger">{error}</Alert>} */}

        {/* 회원가입 버튼 */}
        <Button variant="primary" type="submit" className="w-100">
          회원가입
        </Button>
      </Form>
    </Container>
  )
}

export default Signup
