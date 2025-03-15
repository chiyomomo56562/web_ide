import React from 'react'
import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { persistor, resetContainerInfo, resetUserInfo, Rootstate } from '../../store/store';
import apiClient from '../../api/apiClient';
import Dropdown from "react-bootstrap/Dropdown";


const NavigationBar = () => {
    const navigate = useNavigate()
    const userInfo = useSelector((state:Rootstate) => state.userInfo);
    const dispatch = useDispatch();

    const handleLogout = async() => {
      try{
        await apiClient.post("/api/auth/logout", {}, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
          }
        });

        dispatch(resetUserInfo());
        dispatch(resetContainerInfo());
  
        persistor.purge(); // Redux store 초기화
        localStorage.removeItem("accessToken"); // Access Token 삭제
        navigate("/");
        window.location.reload();
        }catch(error) {
          console.error("로그아웃 실패:", error);
        }
    };


  return (
    <Navbar className="bg-body-tertiary">

<Container>
        <Navbar.Brand href="/">WebIde</Navbar.Brand>
        <Navbar.Toggle />
        <Navbar.Collapse className="justify-content-end">
          {/* ✅ 로그인 상태에 따라 드롭다운 표시 */}
          {userInfo.nickname !== "" ? (
            <Dropdown>
              <Dropdown.Toggle
                variant="link"
                id="dropdown-user"
                style={{
                  border: "none",
                  background: "none",
                  textDecoration: "none",
                  color: "black",
                  cursor: "pointer",
                }}
              >
                {userInfo.nickname}
              </Dropdown.Toggle>

              <Dropdown.Menu align="end">
                <Dropdown.Item onClick={() => navigate("profile")}>
                  프로필
                </Dropdown.Item>
                <Dropdown.Item onClick={handleLogout}>로그아웃</Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>
          ) : (
            // 로그인하지 않은 경우
            <Navbar.Text
              style={{ cursor: "pointer" }}
              onClick={() => navigate("login")}
            >
              로그인
            </Navbar.Text>
          )}
        </Navbar.Collapse>
      </Container>
      </Navbar>
  )
}

export default NavigationBar
