import React from 'react'
import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import { useNavigate } from 'react-router-dom';

const NavigationBar = () => {
    let navigator = useNavigate()
  return (
    <Navbar className="bg-body-tertiary">
        <Container>
          <Navbar.Brand href="/" >WebIde</Navbar.Brand>
          <Navbar.Toggle />
          <Navbar.Collapse className="justify-content-end">
            <Navbar.Text style={{cursor: "pointer"}} onClick={()=>{
                navigator('login');
                //로그인만 있는게 아니라 로그인 된 상태면 profile이 아야함
                //useEffect를 사용해서 state로 나눠야할듯
            }}>
              로그인
            </Navbar.Text>
          </Navbar.Collapse>
        </Container>
      </Navbar>
  )
}

export default NavigationBar
