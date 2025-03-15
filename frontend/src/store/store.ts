import { configureStore, createSlice, PayloadAction, combineReducers } from '@reduxjs/toolkit';
import storage from 'redux-persist/lib/storage';
import { TabInterface, TabInterfaceArr } from '../interface/TabsInterface';
import { persistReducer, persistStore } from 'redux-persist';

const tabInit: TabInterfaceArr ={
    tabArr:[]
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
            },
            updateTabContent: (state, action: PayloadAction<{tabId: number, content: string}>)=>{
                state.tabArr[action.payload.tabId].content = action.payload.content;
            }
        },
});
export const {addNewTab, removeTabs, updateTabContent} = tabs.actions;

const activeTab = createSlice({
    name: "activeTab",
    initialState: {activeIndex:0},
    reducers:{
        setActiveIndex: (state, action: PayloadAction<number>)=>{
            state.activeIndex = action.payload;
        }
    }
})
export const {setActiveIndex} = activeTab.actions;

const userInfo = createSlice({
    name: "userInfo",
    initialState: {id: 0, email: "", nickname: ""},
    reducers:{
        setUserInfo: (state, action: PayloadAction<{id: number, email: string, nickname: string}>)=>{
            state.id = action.payload.id;
            state.email = action.payload.email;
            state.nickname = action.payload.nickname;
        },
        resetUserInfo: (state)=>{
            state.id = 0;
            state.email = "";
            state.nickname = "";
        }
    } 
})

export const {setUserInfo,resetUserInfo} = userInfo.actions;

const containerInfo = createSlice({
    name: "containerInfo",
    initialState: {name:"", state: "stopped", url:"", websocketConnected: false},
    reducers:{
        setContainerInfo: (state, action) => {
            state.name = action.payload.name;
            state.state = action.payload.state;
            state.url = action.payload.url;
        },
        updateContainerState: (state, action) => {
            state.state = action.payload;
        },
        updateWebSocketStatus: (state, action) => {
            state.websocketConnected = action.payload;
        },
        resetContainerInfo: (state) => {
            state.name = "";
            state.state = "stopped";
            state.url = "";
            state.websocketConnected = false;
        },
    },
})

export const {
    setContainerInfo,
    updateContainerState,
    updateWebSocketStatus,
    resetContainerInfo,
} = containerInfo.actions;

const userPersistConfig = {
    key: 'userInfo',
    storage,
    whitelist: ['username', 'email', 'nickname']
};

const containerInfoPersistConfig = {
    key: 'containerInfo',
    storage,
    whitelist: ['name', 'state', 'url', 'websocketConnected'] //유지할 데이터
}

const rootReducer = combineReducers({
    tabs: tabs.reducer,
    activeTab: activeTab.reducer,
    userInfo: persistReducer(userPersistConfig, userInfo.reducer),
    containerInfo: persistReducer(containerInfoPersistConfig, containerInfo.reducer),
});

const store = configureStore({
    reducer: rootReducer,
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
          serializableCheck: {
            // redux-persist 관련 액션들은 직렬화 검사에서 제외합니다.
            ignoredActions: [
              'persist/PERSIST',
              'persist/REHYDRATE',
              'persist/FLUSH',
              'persist/PAUSE',
              'persist/REGISTER',
            ],
          },
        }),
});

export const persistor = persistStore(store);

export type Rootstate = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;