import axios from 'axios';
import React, { useState } from 'react'
import { Container, Form, Button, Card } from "react-bootstrap";
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const navigator = useNavigate();
  const [credentials, setCredentials] = useState({ username: '', password: '' });


  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCredentials({
      ...credentials,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    console.log(credentials)
    axios.post('api/auth/login', credentials, { withCredentials: true })
      .then((res) => {
        console.log(res.data);
        localStorage.setItem("accessToken", res.data.accessToken);
        navigator('/');
      })
      .catch((err) => {
        console.error(err);
      });
  };

  return (
    <Container className="d-flex justify-content-center align-items-center vh-100">
      <Card style={{ width: "350px", padding: "20px" }}>
        <Card.Body>
            <h3 className="text-center">로그인</h3>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="formBasicId">
              <Form.Label>ID</Form.Label>
              <Form.Control
                type="text"
                placeholder="ID를 입력하세요"
                name="username"
                value={credentials.username}
                onChange={handleChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="formBasicPassword">
              <Form.Label>비밀번호</Form.Label>
              <Form.Control
                type="password"
                placeholder="비밀번호를 입력하세요"
                name="password"
                value={credentials.password}
                onChange={handleChange}
                required
              />
            </Form.Group>

            <Button variant="primary" type="submit" className="w-100">
              로그인
            </Button>
          </Form>

          <hr />

          <Button
            variant="outline-dark"
            className="w-100 mb-2"
            // onClick={handleOAuthLogin}
          >
            OAuth 로그인
          </Button>

          <Button variant="link" className="w-100" onClick={()=>navigator('/signup')}>
            회원가입
          </Button>
        </Card.Body>
      </Card>
    </Container>
  )
}

export default Login
