import React from 'react'
import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { Rootstate } from '../../store/store';
import apiClient from '../../api/apiClient';


const NavigationBar = () => {
    const navigate = useNavigate()
    const userInfo = useSelector((state:Rootstate) => state.userInfo);
    console.log(userInfo)

    const handleLogout = () => {
      // 로그아웃은 여기에서 만들거임
      apiClient.post("api/auth/logout").then(() => {
        localStorage.removeItem("accessToken"); //Access Token 삭제
        window.location.href = "/"; //메인 페이지로 리다이렉트
      }).catch(error => {
        console.error("로그아웃 실패:", error);
      });
    };


  return (
    <Navbar className="bg-body-tertiary">
        <Container>
          <Navbar.Brand href="/" >WebIde</Navbar.Brand>
          <Navbar.Toggle />
          <Navbar.Collapse className="justify-content-end">
            <Navbar.Text style={{cursor: "pointer"}} onClick={()=>{
                if(userInfo.nickname !== "") {
                  // 로그인 된 상태이면 프로필 페이지로 이동
                  navigate('profile');
                } else {
                  // 로그인되지 않은 상태이면 로그인 페이지로 이동
                  navigate('login');
                }
            }}>
              { userInfo.nickname !== "" ? userInfo.nickname : "로그인" }
            </Navbar.Text>
          </Navbar.Collapse>
        </Container>
      </Navbar>
  )
}

export default NavigationBar
