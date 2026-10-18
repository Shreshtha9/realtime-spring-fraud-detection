const TX_API = import.meta.env.VITE_TX_API || 'http://localhost:8081'
const FRAUD_API = import.meta.env.VITE_FRAUD_API || 'http://localhost:8082'

async function request(base, path, options = {}) {
  const res = await fetch(`${base}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!res.ok) throw new Error(`Request failed: ${res.status}`)
  const text = await res.text()
  return text ? JSON.parse(text) : null
}

export const getAlerts = (limit = 50) => request(TX_API, `/api/alerts?limit=${limit}`)
export const getStats = () => request(TX_API, '/api/stats')
export const getTransactions = (limit = 50) => request(TX_API, `/api/transactions?limit=${limit}`)
export const getSimulatorStatus = () => request(TX_API, '/api/simulator/status')
export const startSimulator = () => request(TX_API, '/api/simulator/start', { method: 'POST' })
export const stopSimulator = () => request(TX_API, '/api/simulator/stop', { method: 'POST' })
export const runScenario = (type) =>
  request(TX_API, `/api/simulator/scenario/${type}`, { method: 'POST' })
export const createPayment = (payment) =>
  request(TX_API, '/api/transactions', { method: 'POST', body: JSON.stringify(payment) })
export const getRules = () => request(FRAUD_API, '/api/rules')

export const alertStreamUrl = `${TX_API}/api/alerts/stream`