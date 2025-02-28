import React from 'react'
import { Container, Row, Col, Dropdown, DropdownButton, Pagination } from "react-bootstrap";
import { useState } from 'react';
import ShowContainer from '../../component/ShowContainer/ShowContainer';

const Projects = () => {
    const [containers, setContainers] = useState<number[]>(Array.from({ length: 20 }, (_, i) => i + 1));

  const [sortOrder, setSortOrder] = useState<string>("asc"); // 정렬 방식
  const [currentPage, setCurrentPage] = useState<number>(1); // 현재 페이지
  const containersPerPage = 8; // 한 페이지에 표시할 컨테이너 수

  // 정렬 기준 변경
  const handleSortChange = (order: string) => {
    setSortOrder(order);
    const sortedContainers = [...containers].sort((a, b) =>
      order === "asc" ? a - b : b - a
    );
    setContainers(sortedContainers);
  };

  // 현재 페이지 컨테이너 목록 계산
  const indexOfLastContainer = currentPage * containersPerPage;
  const indexOfFirstContainer = indexOfLastContainer - containersPerPage;
  const currentContainers = containers.slice(indexOfFirstContainer, indexOfLastContainer);

  // 페이지 변경 핸들러
  const handlePageChange = (pageNumber: number) => setCurrentPage(pageNumber);

  return (
    <Container className="mt-4">
      {/* 상단 정렬 버튼 */}
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4>컨테이너 목록</h4>
        <DropdownButton
          id="dropdown-sort-button"
          title={`정렬 기준: ${sortOrder === "asc" ? "ID 오름차순" : "ID 내림차순"}`}
          variant="secondary"
        >
          <Dropdown.Item onClick={() => handleSortChange("asc")}>ID 오름차순</Dropdown.Item>
          <Dropdown.Item onClick={() => handleSortChange("desc")}>ID 내림차순</Dropdown.Item>
        </DropdownButton>
      </div>

      {/* 컨테이너 목록 (간격 조정: g-4) */}
      <Row className="g-4">
        {currentContainers.map((id) => (
          <ShowContainer key={id} id={id} />
        ))}
      </Row>

      {/* 페이지네이션 */}
      <div className="d-flex justify-content-center mt-4">
        <Pagination>
          {[...Array(Math.ceil(containers.length / containersPerPage))].map((_, i) => (
            <Pagination.Item
              key={i + 1}
              active={i + 1 === currentPage}
              onClick={() => handlePageChange(i + 1)}
            >
              {i + 1}
            </Pagination.Item>
          ))}
        </Pagination>
      </div>
    </Container>
  )
}

export default Projects
