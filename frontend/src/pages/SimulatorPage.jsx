import { useEffect, useState } from 'react'
import { Play, Square, Send, Timer, Plane, Smartphone, Banknote, Wallet, KeyRound } from 'lucide-react'
import {
  getSimulatorStatus,
  startSimulator,
  stopSimulator,
  runScenario,
  createPayment,
} from '../api/client'

const SCENARIOS = [
  { type: 'velocity', label: 'Velocity burst', help: '8 payments in 8 seconds', icon: Timer },
  { type: 'travel', label: 'Impossible travel', help: 'Home city, then a far city 10 minutes later', icon: Plane },
  { type: 'device', label: 'New device', help: 'Payment from a device the user never used', icon: Smartphone },
  { type: 'amount', label: 'Big amount', help: '15 times the normal payment', icon: Banknote },
  { type: 'daily', label: 'Daily limit', help: 'Three Rs 40,000 payments within 2 hours', icon: Wallet },
  { type: 'takeover', label: 'Account takeover', help: 'New device + far city + big amount', icon: KeyRound },
]

const CITIES = [
  'Pune', 'Mumbai', 'Delhi', 'Bengaluru', 'Hyderabad',
  'Chennai', 'Kolkata', 'Ahmedabad', 'Jaipur', 'Lucknow',
]

export default function SimulatorPage() {
  const [running, setRunning] = useState(false)
  const [busy, setBusy] = useState(false)
  const [message, setMessage] = useState('')
  const [payment, setPayment] = useState({
    userId: 'U1',
    amount: 500,
    city: 'Mumbai',
    deviceId: 'device-1-a',
  })

  useEffect(() => {
    getSimulatorStatus()
      .then((s) => setRunning(s.running))
      .catch((e) => setMessage(`Cannot reach the backend: ${e.message}`))
  }, [])

  const toggleTraffic = async () => {
    try {
      const result = running ? await stopSimulator() : await startSimulator()
      setRunning(result.running)
      setMessage(result.running ? 'Normal traffic started.' : 'Normal traffic stopped.')
    } catch (e) {
      setMessage(`Error: ${e.message}`)
    }
  }

  const fire = async (type) => {
    setBusy(true)
    try {
      const result = await runScenario(type)
      setMessage(`Scenario "${result.scenario}" sent for user ${result.userId}. Watch the Dashboard.`)
    } catch (e) {
      setMessage(`Error: ${e.message}`)
    } finally {
      setBusy(false)
    }
  }

  const sendPayment = async () => {
    try {
      await createPayment({ ...payment, amount: Number(payment.amount) })
      setMessage(`Payment of Rs ${payment.amount} sent for ${payment.userId}.`)
    } catch (e) {
      setMessage(`Error: ${e.message}`)
    }
  }

  const change = (field) => (e) => setPayment({ ...payment, [field]: e.target.value })

  return (
    <div className="page">
      <h2>Simulator</h2>
      <p className="muted">Create traffic and trigger each type of fraud on demand.</p>
      {message && <div className="notice">{message}</div>}

      <div className="panel">
        <div className="traffic-row">
          <div>
            <h3 className="panel-title">Normal traffic</h3>
            <div className="run-status">
              <span className={`run-dot ${running ? 'on' : ''}`} />
              {running ? 'Running: ordinary payments from 500 users' : 'Stopped'}
            </div>
          </div>
          <button className={running ? 'btn btn-danger' : 'btn btn-primary'} onClick={toggleTraffic}>
            {running ? <Square size={16} /> : <Play size={16} />}
            {running ? 'Stop traffic' : 'Start traffic'}
          </button>
        </div>
      </div>

      <div className="panel">
        <h3 className="panel-title">Fraud scenarios</h3>
        <div className="scenario-grid">
          {SCENARIOS.map((s) => {
            const Icon = s.icon
            return (
              <button key={s.type} className="scenario-card" onClick={() => fire(s.type)} disabled={busy}>
                <div className="scenario-icon"><Icon size={20} /></div>
                <div className="scenario-title">{s.label}</div>
                <div className="muted small">{s.help}</div>
              </button>
            )
          })}
        </div>
      </div>

      <div className="panel">
        <h3 className="panel-title">Send one payment</h3>
        <p className="muted small">User must be U1 to U500.</p>
        <div className="form-row">
          <input className="input" value={payment.userId} onChange={change('userId')} placeholder="User id" />
          <input className="input" type="number" value={payment.amount} onChange={change('amount')} placeholder="Amount" />
          <select className="input" value={payment.city} onChange={change('city')}>
            {CITIES.map((c) => <option key={c}>{c}</option>)}
          </select>
          <input className="input" value={payment.deviceId} onChange={change('deviceId')} placeholder="Device id" />
          <button className="btn btn-primary" onClick={sendPayment}>
            <Send size={16} /> Send
          </button>
        </div>
      </div>
    </div>
  )
}