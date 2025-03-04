import axios from 'axios'
import React, { Children, useEffect } from 'react'
import { useState } from 'react'
import { Tree, MoveHandler, SimpleTreeData } from 'react-arborist'
import ContextMenu from '../ContextMenu/ContextMenu'
import { NodeApi } from 'react-arborist'
import { useDispatch, useSelector } from 'react-redux'
import { addNewTab, Rootstate, setActiveIndex } from '../../store/store'
import { TabInterface } from '../../interface/TabsInterface'

const Explorer = () => {
    const dispatch = useDispatch();
    const TabsSelector = useSelector((state: Rootstate) => state.tabs)
    const [treeData, setTreeData] = useState<SimpleTreeData[]>([])
    const [contextMenu, setContextMenu] = useState({ x: 0, y: 0, show: false });

    useEffect(()=>{
      // 서버에서 Tree를 받아오는 부분
      axios.get('/testTree.json').then(
        (response)=>{
          setTreeData(response.data)
        }
      ).catch(
        (e)=>console.log(e)
      )
    }, [])

    useEffect(()=>{
      console.log(treeData)
    },[treeData])
      
    const hasTabs = (fileId:string) =>{
      // 현재 탭에 tabId를 가진 탭이 있나 확인
      // 있다면 tabId를 리턴
      // 없다면 -1을 리턴
      const result = TabsSelector.tabArr.filter((tab)=> tab.fileId == Number(fileId))
      
      if(result.length !== 0)
        return result[0].tabId;  
      return -1;
    }

    const handleNodeClick = (node:NodeApi<SimpleTreeData>[]) => {
      // 폴더면 파일을 드롭 아이템 형식으로 열었다 닫았다.
      // 파일이면 새 탭을 생성하거나 탭으로 이동
      
      console.log("전달된 node 값:", node);
      if (node.length<=0)
        return;

      if(node[0].isLeaf == true) { //파일
        //탭으로 이동
        console.log('file')
        // 탭이 이미 존재하나 확인
        const tabIdx = hasTabs(node[0].id)
        if(tabIdx == -1) {
        // 새탭을 생성
          const newTabId = TabsSelector.tabArr.length;
          const newTreeData:TabInterface = { tabId: newTabId, name:node[0].data.name, fileId:Number(node[0].data.id), content: "", language:"javascript"};

          dispatch(addNewTab(newTreeData))
          dispatch(setActiveIndex(newTabId))
        } else {
          // 그 탭으로 이동
          dispatch(setActiveIndex(tabIdx))
        }
      } else { //폴더
        console.log('folder')
        node[0].toggle()
      }
      };


    let [chatId, setChatId] = useCurrentChatId();
    function useCurrentChatId() {
        const [chatId, setChatId] = useState(null);
        return [chatId, setChatId];
    }

    const onMove = (dragIds, parentId, index)=>{
      // 여기에서 axios.post로 보내면 되겠구나~~
      console.log(dragIds, parentId, index)
    }

    const onCreate = () => {

    }

    const handleContextMenu = (e: React.MouseEvent) => {
      e.preventDefault(); //기본 메뉴가 나오는 걸 막는다.
      if (contextMenu.show) {
        setContextMenu((prev) => ({ ...prev, show: false }));
        setTimeout(() => {
          setContextMenu({ x: e.clientX, y: e.clientY, show: true });
        }, 0);
      } else {
        setContextMenu({ x: e.clientX, y: e.clientY, show: true });
      }
    };

  return (
    <>
      {/* 좌측 탐색기 */}
      <h6>탐색기</h6>
      <div style={{ height: "80vh" }} onClick={() => setContextMenu((prev) => ({ ...prev, visible: false }))}>
        <Tree
            data={treeData}
            width={"100%"}
            height={500}
            indent={24}
            rowHeight={36}
            getChildren={(node) => node.children || []}
            onSelect={(node) => handleNodeClick(node)}
            selection={chatId}
            onMove={onMove}
            onCreate={onCreate}
            // onDelete={}

            onContextMenu={handleContextMenu}
          />
          { (
          <ContextMenu 
            x={contextMenu.x} 
            y={contextMenu.y} 
            show={contextMenu.show} 
            onClose={() => setContextMenu(prev => ({ ...prev, show: false }))}
          />
        )}
      </div>
      
    </>
  )
}

export default Explorer