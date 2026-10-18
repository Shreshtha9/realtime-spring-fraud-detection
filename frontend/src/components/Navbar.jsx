import { NavLink } from 'react-router-dom'
import { ShieldCheck, LayoutDashboard, Zap, ArrowLeftRight, ListChecks } from 'lucide-react'

const LINKS = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/simulator', label: 'Simulator', icon: Zap },
  { to: '/transactions', label: 'Transactions', icon: ArrowLeftRight },
  { to: '/rules', label: 'Rules', icon: ListChecks },
]

export default function Navbar() {
  return (
    <nav className="navbar">
      <div className="brand">
        <ShieldCheck size={22} />
        <span>FraudGuard</span>
      </div>
      <div className="nav-links">
        {LINKS.map((link) => {
          const Icon = link.icon
          return (
            <NavLink key={link.to} to={link.to} end={link.end}>
              <Icon size={16} />
              <span>{link.label}</span>
            </NavLink>
          )
        })}
      </div>
    </nav>
  )
}