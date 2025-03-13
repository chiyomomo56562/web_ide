import React, { useEffect, useState } from 'react'
import './Home.css'
import CreateContainer from '../../component/CreateContainer/CreateContainer'
import { Container, Row, Button } from 'react-bootstrap'
import ShowContainer from '../../component/ShowContainer/ShowContainer'
import { useNavigate } from 'react-router-dom'
import { Containers } from '../../interface/Containers'
import apiClient from '../../api/apiClient'

const Home = () => {

  let [containers, setContainers] = useState<Containers[]>([]);
  const [data, setData] = useState([]);
  useEffect(() => {
    apiClient.get('/api/projects', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        'Accept': 'application/json'
      },  
      params: {
        page: 0,
        sorted: "latest",
        limit: 3,
      }
    })
    .then((response) => {  
      console.log(response.data);
      setData(response.data.projects);
    })
    .catch(error => {
      console.error("데이터 요청 실패:", error);
    });
  }, []);

  useEffect(() => {
    /**
     * 데이터가 변경 될 때 실행 한다.
     */
    setContainers([...data.slice(0,3)]);
  }, [data]);
  
  const navigator = useNavigate()
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
