import React from 'react'
import { Col, Button } from 'react-bootstrap'
import './CreateContainer.css'
import { useNavigate } from 'react-router-dom'

const CreateContainer = () => {
  let navigator = useNavigate()
  return (
    <Col lg={3} md={4} sm={6} xs={12}>
        <Button className="container-create-button" variant="outline-primary" onClick={()=>navigator('/newproject')}>
            컨테이너 생성
        </Button>
    </Col>
  )
}

export default CreateContainer
