import { useEffect, useState } from 'react'
import { Activity, Bell, Eye, Ban, CheckCircle } from 'lucide-react'
import { getAlerts, getStats } from '../api/client'
import useAlertStream from '../hooks/useAlertStream'
import StatCard from '../components/StatCard'
import AlertFeed from '../components/AlertFeed'
import RuleBars from '../components/RuleBars'
import DecisionSplit from '../components/DecisionSplit'

export default function DashboardPage() {
  const [alerts, setAlerts] = useState([])
  const [stats, setStats] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getAlerts(50).then(setAlerts).catch((e) => setError(e.message))
  }, [])

  useEffect(() => {
    const load = () => getStats().then(setStats).catch(() => {})
    load()
    const id = setInterval(load, 5000)
    return () => clearInterval(id)
  }, [])

  const connected = useAlertStream((alert) => {
    setAlerts((prev) =>
      prev.some((a) => a.transactionId === alert.transactionId)
        ? prev
        : [alert, ...prev].slice(0, 50)
    )
  })

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Dashboard</h2>
          <p className="muted">Every payment is checked against the fraud rules the moment it arrives.</p>
        </div>
        <div className={`live-pill ${connected ? 'on' : 'off'}`}>
          <span className="dot" />
          {connected ? 'Live' : 'Disconnected'}
        </div>
      </div>

      {error && <p className="error">Cannot reach the backend: {error}</p>}

      <div className="cards">
        <StatCard label="Transactions" value={stats?.totalTransactions} icon={Activity} tone="blue" />
        <StatCard label="Allowed" value={stats?.allowed} icon={CheckCircle} tone="blue" />
        <StatCard label="Alerts" value={stats?.totalAlerts} icon={Bell} tone="purple" />
        <StatCard label="In review" value={stats?.review} icon={Eye} tone="amber" />
        <StatCard label="Blocked" value={stats?.blocked} icon={Ban} tone="red" />
      </div>

      <div className="grid-2">
        <div className="panel">
          <h3 className="panel-title">Live alerts</h3>
          <AlertFeed alerts={alerts} />
        </div>
        <div className="side-column">
          <DecisionSplit review={stats?.review} blocked={stats?.blocked} />
          <RuleBars alerts={alerts} />
        </div>
      </div>
    </div>
  )
}