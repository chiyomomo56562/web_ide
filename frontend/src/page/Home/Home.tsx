import React, { useEffect, useState } from 'react'
import './Home.css'
import CreateContainer from '../../component/CreateContainer/CreateContainer'
import { Container, Row, Col, Button } from 'react-bootstrap'
import ShowContainer from '../../component/ShowContainer/ShowContainer'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import { Containers } from '../../interface/Containers'

const Home = () => {

  let [containers, setContainers] = useState<Containers[]>([]);

  useEffect(() => {
    axios.get('/testContainer.json', {
      params: {
        page: 1,
        sorted: "latest",
        limit: 3,
      }
    })
    .then((response) => {  
      setContainers([...response.data.slice(0,3)]);
    })
    .catch(error => {
      console.error("데이터 요청 실패:", error);
    });
  }, []);

  let navigator = useNavigate()
  return (
    <div>
       <Container className="d-flex flex-column align-items-center vh-100 pt-5">
        <div className="position-relative w-100 text-end pe-3">
          <Button className="border-dark rounded-0 container-view-button" variant="outline-primary" onClick={()=>navigator('projects')}>전체 컨테이너 보기</Button>
        </div>
        <Row className="g-4 text-center mt-3 w-100">
          <CreateContainer />
          {/* 
            완성 후 container로 바꿔줘야한다.
          */}
          
          {containers.map((item, id) => (
            <ShowContainer item={item} key={id}/>
          ))}
        </Row>
      </Container>
    </div>
  )
}

export default Home
