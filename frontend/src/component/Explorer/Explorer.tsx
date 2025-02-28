import React from 'react'
import { Col } from 'react-bootstrap'

const Explorer = () => {
  return (
    <Col md={2} className="bg-dark text-light p-3 border-end d-flex flex-column" style={{ overflowY: "auto" }}>
            <h6 className="mb-3">탐색기</h6>
            <ul className="list-unstyled">
              <li> frontend</li>
              <ul className="ps-3">
                <li> 파일1.c</li>
                <li> 파일2.py</li>
                <li> 파일3.js</li>
              </ul>
            </ul>
    </Col>
  )
}

export default Explorer
