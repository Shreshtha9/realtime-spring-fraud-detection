import { Plane, Banknote, Timer, Wallet, Smartphone, ShieldAlert } from 'lucide-react'

export const RULE_META = {
  IMPOSSIBLE_TRAVEL: { label: 'Impossible travel', icon: Plane },
  UNUSUAL_AMOUNT: { label: 'Unusual amount', icon: Banknote },
  VELOCITY: { label: 'Velocity', icon: Timer },
  DAILY_LIMIT: { label: 'Daily limit', icon: Wallet },
  NEW_DEVICE: { label: 'New device', icon: Smartphone },
}

export function ruleMeta(name) {
  return RULE_META[name] || { label: name, icon: ShieldAlert }
}

// "VELOCITY: 6 payments in 60 seconds" -> { name: 'VELOCITY', text: '6 payments in 60 seconds' }
export function parseReason(reason) {
  const index = reason.indexOf(': ')
  if (index === -1) return { name: '', text: reason }
  return { name: reason.slice(0, index), text: reason.slice(index + 2) }
}