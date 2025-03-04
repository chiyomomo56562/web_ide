import React, { useEffect, useState } from "react";
import Editor from "@monaco-editor/react";
import { Tab, Tabs, TabList, TabPanel } from "react-tabs";
import "react-tabs/style/react-tabs.css"; // 기본 스타일
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, removeTabs, Rootstate, setActiveIndex } from "../../store/store";


const CodeEditor = () => {
  const tabsSelector = useSelector((state:Rootstate)=>state.tabs);
  const activeIndex = useSelector((state:Rootstate)=>state.activeTab);

  const dispatch = useDispatch<AppDispatch>();

  const handleEditorChange = (value: string | undefined, index: number) => {
    setTabs((prevTabs) =>
      prevTabs.map((tab, i) => (i === index ? { ...tab, content: value || "" } : tab))
    );
  };

  const removeTab = (index: number) => {
    dispatch(removeTabs(index));
    const countTabs = tabsSelector.tabArr.length - 2
    if (activeIndex >= countTabs) {
      //activeIndex가 지금의 length보다 길다면 줄여준다.
      console.log(activeIndex, countTabs)
      dispatch(setActiveIndex(Math.max(0, countTabs)));
    }
  };

  return (
    <div className="bg-dark text-light" style={{ display: "flex", flexDirection: "column", height: "100vh" }}>
      {/* 탭 UI */}
      <Tabs selectedIndex={activeIndex} onSelect={(index)=>{dispatch(setActiveIndex(index))}}>
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
              language={tab.language}
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