import { useEffect, useState } from 'react'
import { getRules } from '../api/client'
import { ruleMeta } from '../utils/rules'

export default function RulesPage() {
  const [rules, setRules] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    getRules().then(setRules).catch((e) => setError(e.message))
  }, [])

  return (
    <div className="page">
      <h2>Fraud rules</h2>
      <p className="muted">Each rule adds its score when it fires. A new rule shows up here automatically.</p>
      {error && <p className="error">Cannot reach the fraud service: {error}</p>}

      <div className="rule-grid">
        {rules.map((r, i) => {
          const { label, icon: Icon } = ruleMeta(r.name)
          return (
            <div key={r.name} className="rule-card" style={{ animationDelay: `${i * 70}ms` }}>
              <div className="rule-head">
                <div className="scenario-icon"><Icon size={20} /></div>
                <span>{label}</span>
              </div>
              <p className="rule-desc">{r.description}</p>
              <div className="rule-score">
                <div className="bar-track">
                  <div className="bar-fill" style={{ width: `${r.score}%` }} />
                </div>
                <b>{r.score}</b>
              </div>
            </div>
          )
        })}
      </div>

      <div className="panel">
        <h3 className="panel-title">How the decision is made</h3>
        <p className="muted">The scores of every rule that fires are added up and capped at 100.</p>
        <div className="bands">
          <div className="band band-allow"><b>ALLOW</b>Total below 30</div>
          <div className="band band-review"><b>REVIEW</b>Total from 30 to 69</div>
          <div className="band band-block"><b>BLOCK</b>Total of 70 or more</div>
        </div>
      </div>
    </div>
  )
}