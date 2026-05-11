import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export function ProtectedRoute({ children }) {
  const { token } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  return children
}

export function AdminRoute({ children }) {
  const { token, user } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  if (!user?.isAdmin) return <Navigate to="/" replace />
  return children
}

export function SellerRoute({ children }) {
  const { token, user } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  if (!user?.isSeller && !user?.isAdmin) return <Navigate to="/" replace />
  return children
}
