import React from 'react'
import './Home.css'
import CreateContainer from '../../component/CreateContainer/CreateContainer'
import { Container, Row, Col, Button } from 'react-bootstrap'
import ShowContainer from '../../component/ShowContainer/ShowContainer'
import { useNavigate } from 'react-router-dom'

const Home = () => {
  let navigator = useNavigate()
  return (
    <div>
       <Container className="d-flex flex-column align-items-center vh-100 pt-5">
        <div className="position-relative w-100 text-end pe-3">
          <Button className="border-dark rounded-0 container-view-button" variant="outline-primary" onClick={()=>navigator('projects')}>전체 컨테이너 보기</Button>
        </div>
        <Row className="g-4 text-center mt-3 w-100">
          <CreateContainer />
          {[1, 2, 3].map((id) => (
            <ShowContainer key={id} id={id}/>
          ))}
        </Row>
      </Container>
    </div>
  )
}

export default Home
