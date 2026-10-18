import DecisionBadge from './DecisionBadge'

export default function AlertTable({ alerts }) {
  if (alerts.length === 0) {
    return <p className="muted">No alerts yet.</p>
  }
  return (
    <table className="table">
      <thead>
        <tr>
          <th>Time</th><th>User</th><th>Score</th><th>Decision</th><th>Reasons</th>
        </tr>
      </thead>
      <tbody>
        {alerts.map((a) => (
          <tr key={a.transactionId}>
            <td>{new Date(a.createdAt).toLocaleTimeString()}</td>
            <td>{a.userId}</td>
            <td>{a.riskScore}</td>
            <td><DecisionBadge decision={a.decision} /></td>
            <td>
              <ul className="reasons">
                {a.reasons.map((r, i) => <li key={i}>{r}</li>)}
              </ul>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}