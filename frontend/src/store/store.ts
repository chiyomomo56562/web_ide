import { configureStore, createSlice, current, PayloadAction } from '@reduxjs/toolkit';
import { TabInterface, TabInterfaceArr } from '../interface/TabsInterface';

const tabInit: TabInterfaceArr ={
    tabArr:
    [
        { tabId:0, fileId: 2, name: "index.js", content: "// JavaScript Code", language: "javascript" },
        { tabId:1, fileId: 3, name: "App.js", content: "app.js", language: "javascript" },
    ]
}

const tabs = createSlice({
        name:'tabs',
        initialState: tabInit, // initaial state를 서버에서 받아올 이유가 없다. 빈칸에서 시작
        reducers: {
            addNewTab: (state, action: PayloadAction<TabInterface>)=>{
                state.tabArr.push(action.payload)
            },
            removeTabs: (state, action: PayloadAction<number>)=>{
                const filteredState:TabInterface[] = state.tabArr.filter((item)=>{
                    return item.tabId !== action.payload
                })
                // 삭제 후 ID를 다시 세팅해줘야한다.
                const newState = filteredState.map((item, i)=>{
                    return {...item, tabId: i}
                })
                state.tabArr = [...newState]
            }
        },
});
export const {addNewTab, removeTabs} = tabs.actions;

const activeTab = createSlice({
    name: "activeTab",
    initialState: 0,
    reducers:{
        setActiveIndex: (state, action: PayloadAction<number>)=>{
            console.log("setActiveIndex:", action.payload)
            return action.payload
        }
    }
})
export const {setActiveIndex} = activeTab.actions;

const userInfo = createSlice({
    name: "userInfo",
    initialState: {username: "", email: "", token: ""},
    reducers:{} // action을 추가해야함
})

const store = configureStore({
    reducer: {
        tabs: tabs.reducer,
        activeTab: activeTab.reducer,
        userInfo: userInfo.reducer,
    },
  })

export type Rootstate = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;