import { configureStore, createSlice } from '@reduxjs/toolkit';
let temp = createSlice({
        name:'temp',
        initialState: 'temp',
        reducers: {

        },
});
export default configureStore({
    reducer: {
        temp: temp.reducer,

    },
  })