import { ShieldCheck, Eye, Ban } from 'lucide-react'

const ICONS = { ALLOW: ShieldCheck, REVIEW: Eye, BLOCK: Ban }

export default function DecisionBadge({ decision }) {
  const Icon = ICONS[decision] || Eye
  return (
    <span className={`badge badge-${decision.toLowerCase()}`}>
      <Icon size={12} />
      {decision}
    </span>
  )
}