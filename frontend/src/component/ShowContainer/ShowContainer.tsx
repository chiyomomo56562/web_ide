import React from 'react'
import { Col, Button } from 'react-bootstrap'
import './ShowContainer.css'
import { useNavigate } from 'react-router-dom'

interface ShowContainerProps{
  id: number;
}

const ShowContainer = ({id}:ShowContainerProps) => {
  const navigator = useNavigate()

  return (
    <Col lg={3} md={4} sm={6} xs={12} key={id}>
      <div className="container-wrapper">
        <Button className="container-button" variant="outline-secondary" onClick={()=>navigator('/editor')}>
          <div className="container-text">컨테이너</div>
        </Button>
        <Button variant="secondary" size="sm" className="settings-button" onClick={()=>navigator('/ProjectSettings')}>설정</Button>
        {/* projectSettings가 어느 프로젝트의 설정인지 알 수 없는 상태
          기능을 만들때 확실히 수정해줘야한다.
        */}
      </div>
    </Col>
  )
}

export default ShowContainer
