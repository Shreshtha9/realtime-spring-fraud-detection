import useCountUp from '../hooks/useCountUp'

export default function StatCard({ label, value, icon, tone = 'blue' }) {
  const animated = useCountUp(value)
  const Icon = icon

  return (
    <div className={`stat-card tone-${tone}`}>
      <div className="stat-icon">
        <Icon size={22} />
      </div>
      <div>
        <div className="stat-value">
          {value == null ? '-' : animated.toLocaleString('en-IN')}
        </div>
        <div className="stat-label">{label}</div>
      </div>
    </div>
  )
}