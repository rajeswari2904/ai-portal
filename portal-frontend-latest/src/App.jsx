import './App.css'
import Login from './pages/login/Login'
import Home from './pages/home/Home'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import ProtectedRoute from './utils/ProtectedRoute'

function App() {
  return (
      <div>
        <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route
            path="/home"element= {<ProtectedRoute><Home /></ProtectedRoute>}
          />
        </Routes>
      </BrowserRouter>
      </div>
  )
}

export default App
