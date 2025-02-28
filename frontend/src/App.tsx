
import { Route, Routes } from 'react-router-dom'
import './App.css'
import 'bootstrap/dist/css/bootstrap.min.css';

import NavigationBar from './component/NavigationBar/NavigationBar';
import Home from './page/Home/Home';
import Login from './page/Login/Login';
import Signup from './page/Signup/Signup';
import Projects from './page/Projects/Projects';
import NewProject from './page/NewProject/NewProject';
import ProjectSettings from './page/ProjectSetting/ProjectSettings';
import IDE from './page/IDE/IDE';

function App() {
  return (
    <>
      <NavigationBar />

      <Routes>
        <Route path="/" element={<Home />}/>
        <Route path="/login" element={<Login />}/>
        <Route path='/signup' element={<Signup />} />
        <Route path='/projects' element={<Projects />} />
        <Route path='/newproject' element={<NewProject />} />
        <Route path='/projectSettings' element={<ProjectSettings />} />
        <Route path='/editor' element={<IDE />}/>
      </Routes>
    </>
  )
}

export default App
