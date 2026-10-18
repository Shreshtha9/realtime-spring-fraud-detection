import { ruleMeta, parseReason } from '../utils/rules'

export default function RuleBars({ alerts }) {
  const counts = {}
  alerts.forEach((a) =>
    a.reasons.forEach((r) => {
      const { name } = parseReason(r)
      if (name) counts[name] = (counts[name] || 0) + 1
    })
  )
  const rows = Object.entries(counts).sort((a, b) => b[1] - a[1])
  const max = rows.length ? rows[0][1] : 1

  return (
    <div className="panel">
      <h3 className="panel-title">Alerts by rule</h3>
      <p className="muted small">Based on the latest {alerts.length} alerts</p>
      {rows.length === 0 && <p className="muted">No data yet.</p>}
      <div className="bars">
        {rows.map(([name, count]) => {
          const { label, icon: Icon } = ruleMeta(name)
          return (
            <div key={name} className="bar-row">
              <div className="bar-label"><Icon size={14} /> {label}</div>
              <div className="bar-track">
                <div className="bar-fill" style={{ width: `${(count / max) * 100}%` }} />
              </div>
              <div className="bar-count">{count}</div>
            </div>
          )
        })}
      </div>
    </div>
  )
}