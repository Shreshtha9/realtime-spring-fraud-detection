import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import DashboardPage from './pages/DashboardPage'
import SimulatorPage from './pages/SimulatorPage'
import TransactionsPage from './pages/TransactionsPage'
import RulesPage from './pages/RulesPage'

export default function App() {
  return (
    <>
      <Navbar />
      <main className="container">
        <Routes>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/simulator" element={<SimulatorPage />} />
          <Route path="/transactions" element={<TransactionsPage />} />
          <Route path="/rules" element={<RulesPage />} />
        </Routes>
      </main>
    </>
  )
}