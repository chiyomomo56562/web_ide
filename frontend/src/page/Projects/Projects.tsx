import React, { useEffect } from 'react'
import { Container, Row, Dropdown, DropdownButton  } from "react-bootstrap";
import { useState } from 'react';
import ShowContainer from '../../component/ShowContainer/ShowContainer';
// import context from 'react-bootstrap/esm/AccordionContext';
import CreateContainer from '../../component/CreateContainer/CreateContainer';
import { Containers } from '../../interface/Containers';
import apiClient from '../../api/apiClient';

const Projects = () => {
  const [page, setPage] = useState<number>(1);
  const [containers, setContainers] = useState<Containers[]>([]);
  const [totalPages, setToatlPages] = useState<number>(0);
  const [sortOrder, setSortOrder] = useState<string>("latest"); // 정렬 방식
  const [currentPage, setCurrentPage] = useState<number>(0); // 현재 페이지

  useEffect( () => {
    {/*
      프로젝트를 7개 출력한다
      프로젝트는 서버에서 정렬해서 보여줄거니
      그냥 요청만 하면 될 거 같음
      id랑 name을 요청해서 가져오자
    */}
    apiClient.get('/api/projects', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        'Accept': 'application/json'
      },
      params: {
        page: currentPage, //페이지
        sorted: "latest",//최근 수정한 순서
        limit: 7, //7개 요청
      }
    }).then(
      (response)=>{
        console.log(response.data);
        setToatlPages(response.data[0]-1);
        setContainers([...response.data[1].slice(0,7)]); //7개만 가져오기
      }
    ).catch(
      (error)=>{
        console.log(error);
      }
    )
  }, [currentPage]) 

  // 페이지 변경 핸들러
  const handlePageChange = (pageNumber: number) => setCurrentPage(pageNumber);

  return (
    <Container className="mt-4">
      {/* 상단 정렬 버튼 */}
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4>프로젝트 목록</h4>
        <DropdownButton
          id="dropdown-sort-button"
          title={`정렬 기준: ${sortOrder === "latest" ? "최근 편집 순" : "편집된 지 오래된 순"}`}
          variant="secondary"
        >
          <Dropdown.Item onClick={() => setSortOrder("latest")}>최근 편집 순</Dropdown.Item>
          <Dropdown.Item onClick={() => setSortOrder("oldest")}>편집된 지 오래된 순</Dropdown.Item>
        </DropdownButton>
      </div>

      {/* 컨테이너 목록 (간격 조정: g-4) */}
      <Row className="g-4">
        <CreateContainer />
        {containers.map((item, id) => (
          <ShowContainer key={id} item={item} />
        ))}
      </Row>

      {/* 페이지네이션 */}
      {/* 📌 페이지네이션 버튼 */}
      <div>
        <button
          disabled={currentPage === 0}
          onClick={() => setCurrentPage((current) => current - 1)}
        >
          이전
        </button>

        {[...Array(totalPages)].map((_, i) => (
          <button
            key={i + 1}
            onClick={() => setCurrentPage(i)}
            disabled={currentPage === i}
          >
            {i+1}
          </button>
        ))}

        <button
          disabled={currentPage === totalPages}
          onClick={() => setCurrentPage((current) => current + 1)}
        >
          다음
        </button>
      </div>
    </Container>
  )
}

export default Projects
