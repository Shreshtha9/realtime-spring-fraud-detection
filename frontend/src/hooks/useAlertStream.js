import { useEffect, useState } from 'react'
import { alertStreamUrl } from '../api/client'

export default function useAlertStream(onAlert) {
  const [connected, setConnected] = useState(false)

  useEffect(() => {
    const source = new EventSource(alertStreamUrl)
    source.onopen = () => setConnected(true)
    source.onerror = () => setConnected(false)
    source.addEventListener('alert', (e) => onAlert(JSON.parse(e.data)))
    return () => source.close()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  return connected
}