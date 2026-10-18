export default function DecisionSplit({ review = 0, blocked = 0 }) {
  const total = review + blocked
  const reviewPct = total ? Math.round((review / total) * 100) : 0
  const blockPct = total ? 100 - reviewPct : 0

  return (
    <div className="panel">
      <h3 className="panel-title">Decision split</h3>
      <div className="split-bar">
        <div className="split-review" style={{ width: `${reviewPct}%` }} />
        <div className="split-block" style={{ width: `${blockPct}%` }} />
      </div>
      <div className="split-legend">
        <span><i className="dot-review" />Review {review}</span>
        <span><i className="dot-block" />Blocked {blocked}</span>
      </div>
    </div>
  )
}