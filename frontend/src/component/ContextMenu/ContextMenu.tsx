import React, { useRef, useEffect } from 'react';
import { Overlay, Popover } from 'react-bootstrap';

interface ContextMenuProps {
  x: number;
  y: number;
  show: boolean;
  onClose: () => void;
}

const ContextMenu: React.FC<ContextMenuProps> = ({ x, y, show, onClose }) => {
  const target = useRef<HTMLDivElement>(null);

  // 메뉴 외부 클릭 시 메뉴 닫기
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (target.current && !target.current.contains(event.target as Node)) {
        onClose();
      }
    };
    if (show) {
      document.addEventListener('click', handleClickOutside);
    }
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, [show, onClose]);

  return (
    <div
      onContextMenu={(e) => e.preventDefault()} // 기본 메뉴 방지
      ref={target}
      style={{
        position: 'absolute',
        top: y,
        left: x,
        zIndex: 2000,
      }}
    >
      <Overlay target={target.current} show={show} placement="right">
        <Popover id="context-menu-popover">
          <Popover.Body>
            <ul className="list-unstyled mb-0">
              <li>
                <button 
                  className="btn btn-link" 
                  onClick={() => { onClose(); console.log("Option 1 선택"); }}>
                  새파일
                </button>
              </li>
              <li>
                <button 
                  className="btn btn-link" 
                  onClick={() => { onClose(); console.log("Option 2 선택"); }}>
                  새폴더
                </button>
              </li>
              <li>
                <button 
                  className="btn btn-link" 
                  onClick={() => { onClose(); console.log("Option 3 선택"); }}>
                  이름 바꾸기
                </button>
              </li>
            </ul>
          </Popover.Body>
        </Popover>
      </Overlay>
    </div>
  );
};

export default ContextMenu;