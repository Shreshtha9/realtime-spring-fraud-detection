import { useEffect, useState } from 'react'
import { ShieldCheck } from 'lucide-react'
import DecisionBadge from './DecisionBadge'
import { ruleMeta, parseReason } from '../utils/rules'
import { timeAgo } from '../utils/time'

export default function AlertFeed({ alerts }) {
  const [, setTick] = useState(0)

  // re-render every 10 seconds so "2 min ago" stays correct
  useEffect(() => {
    const id = setInterval(() => setTick((t) => t + 1), 10000)
    return () => clearInterval(id)
  }, [])

  if (alerts.length === 0) {
    return (
      <div className="empty">
        <ShieldCheck size={36} />
        <p>All quiet. No alerts yet.</p>
      </div>
    )
  }

  return (
    <div className="feed">
      {alerts.map((a) => {
        const level = a.decision.toLowerCase()
        return (
          <div key={a.transactionId} className={`alert-item ${level}`}>
            <div className="alert-top">
              <span className="chip">{a.userId}</span>
              <DecisionBadge decision={a.decision} />
              <div className="meter" title={`Risk score ${a.riskScore}`}>
                <div className={`meter-fill ${level}`} style={{ width: `${a.riskScore}%` }} />
              </div>
              <span className="score">{a.riskScore}</span>
              <span className="ago">{timeAgo(a.createdAt)}</span>
            </div>
            <ul className="reason-list">
              {a.reasons.map((r, i) => {
                const { name, text } = parseReason(r)
                const { label, icon: Icon } = ruleMeta(name)
                return (
                  <li key={i}>
                    <Icon size={14} />
                    <span><b>{label}</b>{text}</span>
                  </li>
                )
              })}
            </ul>
          </div>
        )
      })}
    </div>
  )
}