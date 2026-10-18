import { useEffect, useState } from 'react'
import { getTransactions } from '../api/client'

export default function TransactionsPage() {
  const [rows, setRows] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    const load = () =>
      getTransactions(50)
        .then(setRows)
        .catch((e) => setError(e.message))
    load()
    const id = setInterval(load, 5000)
    return () => clearInterval(id)
  }, [])

  return (
    <>
      <h2>Transactions</h2>
      <p className="muted">The 50 most recent payments. Refreshes every 5 seconds.</p>
      {error && <p className="error">Cannot reach the backend: {error}</p>}

      <table className="table">
        <thead>
          <tr>
            <th>Time</th><th>User</th><th>Amount</th><th>City</th><th>Device</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((t) => (
            <tr key={t.transactionId}>
              <td>{new Date(t.eventTime).toLocaleTimeString()}</td>
              <td>{t.userId}</td>
              <td>Rs {Number(t.amount).toLocaleString('en-IN')}</td>
              <td>{t.city}</td>
              <td>{t.deviceId}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  )
}