import React, { useEffect, useState } from "react";
import Editor from "@monaco-editor/react";
import { Tab, Tabs, TabList, TabPanel } from "react-tabs";
import "react-tabs/style/react-tabs.css"; // 기본 스타일
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, removeTabs, Rootstate, setActiveIndex, updateTabContent } from "../../store/store";
import apiClient from "../../api/apiClient";


const CodeEditor = ({containerName}:{containerName:string}) => {
  const tabsSelector = useSelector((state:Rootstate)=>state.tabs);
  const activeTAbSelector = useSelector((state:Rootstate)=>state.activeTab);

  const dispatch = useDispatch<AppDispatch>();

  const handleEditorChange = (value: string | undefined, index: number) => {
    dispatch(updateTabContent({ tabId: index, content: value || "" }));
  };

  const removeTab = (index: number) => {
    dispatch(removeTabs(index));
    const countTabs = tabsSelector.tabArr.length - 2
    if (activeTAbSelector.activeIndex >= countTabs) {
      //activeIndex가 지금의 length보다 길다면 줄여준다.
      console.log(activeTAbSelector.activeIndex, countTabs)
      dispatch(setActiveIndex(Math.max(0, countTabs)));
    }
  };

  const saveCode = async () => {
    if (activeTAbSelector.activeIndex >= 0 && activeTAbSelector.activeIndex < tabsSelector.tabArr.length) {
      const activeTab = tabsSelector.tabArr[activeTAbSelector.activeIndex];

      const payload = {
        containerName: containerName, // 🔥 현재 컨테이너 ID
        filePath: activeTab.root, // 🔥 컨테이너 내부 경로
        content: activeTab.content,
      };
      try {
        console.log(payload)
        const response = await apiClient.post("/api/files/save", 
          payload,
          {
            headers: {
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
            'Accept': 'application/json'
            }
          });
        console.log("✅ 저장 성공:", response.data);
        alert("✅ 코드가 성공적으로 저장되었습니다!");
      } catch (error) {
        console.error("🚨 저장 실패:", error);
        alert("🚨 저장 중 오류가 발생했습니다!");
      }
    }
  };
  

  // 🔥 Ctrl + S 이벤트 감지 및 저장 로직 실행
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if ((event.ctrlKey || event.metaKey) && event.key === "s") {
        event.preventDefault(); // 기본 저장 기능 방지
        saveCode();
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => {
      window.removeEventListener("keydown", handleKeyDown);
    };
  }, [activeTAbSelector.activeIndex, tabsSelector.tabArr]);

  return (
    <div className="bg-dark text-light" style={{ display: "flex", flexDirection: "column", height: "100vh" }}>
      {/* 탭 UI */}
      <Tabs selectedIndex={activeTAbSelector.activeIndex} 
        onSelect={
          (index)=>{
            dispatch(
              setActiveIndex(index)
            )
          }
        }
      >
        <TabList  className="bg-dark text-light">
          {tabsSelector.tabArr.map((tab) => (
            <Tab key={tab.tabId}>
              {tab.name}{" "}
              <button
                onClick={(e) => {
                  e.stopPropagation(); // 탭 변경 방지
                  removeTab(tab.tabId);
                }}
                style={{ marginLeft: 8, color: "red", border: "none", background: "none", cursor: "pointer", backgroundColor: "none", padding: "10px" }}
              >
                ✖
              </button>
            </Tab>
          ))}
        </TabList>

        {tabsSelector.tabArr.map((tab, index) => (
          <TabPanel key={tab.tabId}>
            <Editor
              height="calc(70vh - 50px)"
              theme="vs-dark"
              value={tab.content}
              onChange={(value) => handleEditorChange(value, index)}
              options={{ fontSize: 14, minimap: { enabled: false } }}
            />
          </TabPanel>
        ))}
      </Tabs>
    </div>
  );
};

export default CodeEditor;