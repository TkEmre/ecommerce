import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft } from 'lucide-react'
import { Button } from '../components/ui/button'
import { Badge } from '../components/ui/badge'
import { Separator } from '../components/ui/separator'
import client from '../api/client'

const STATUS_VARIANT = {
  PENDING:    'warning',
  PROCESSING: 'info',
  SHIPPED:    'info',
  DELIVERED:  'success',
  CANCELED:   'destructive',
  RETURNED:   'secondary',
}

export default function OrderDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)
  const [cancelling, setCancelling] = useState(false)

  useEffect(() => {
    client.get(`/orders/${id}`)
      .then(({ data }) => setOrder(data))
      .catch(() => navigate('/orders'))
      .finally(() => setLoading(false))
  }, [id])

  async function handleCancel() {
    if (!confirm('Cancel this order?')) return
    setCancelling(true)
    try {
      await client.delete(`/orders/${id}`)
      setOrder((o) => ({ ...o, status: 'CANCELED' }))
    } catch (err) {
      alert(err.response?.data?.message || 'Could not cancel order.')
    } finally {
      setCancelling(false)
    }
  }

  if (loading) return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 py-10 space-y-4 animate-pulse">
      <div className="h-5 bg-zinc-100 rounded w-1/3" />
      <div className="h-32 bg-zinc-100 rounded-xl" />
      <div className="h-48 bg-zinc-100 rounded-xl" />
    </div>
  )

  if (!order) return null

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 py-10">
      <button onClick={() => navigate('/orders')}
        className="flex items-center gap-1.5 text-sm text-zinc-500 hover:text-zinc-900 transition-colors mb-8">
        <ArrowLeft className="w-4 h-4" />
        All orders
      </button>

      {/* Header */}
      <div className="flex items-start justify-between mb-6">
        <div>
          <h1 className="text-xl font-semibold text-zinc-900">Order #{order.id}</h1>
          <p className="text-sm text-zinc-400 mt-1">
            {new Date(order.orderDate).toLocaleDateString('en-US', {
              weekday: 'long', year: 'numeric', month: 'long', day: 'numeric',
            })}
          </p>
        </div>
        <Badge variant={STATUS_VARIANT[order.status] || 'secondary'}>{order.status}</Badge>
      </div>

      {/* Items */}
      <div className="border border-zinc-200 rounded-xl overflow-hidden mb-4">
        <div className="px-5 py-3 bg-zinc-50 border-b border-zinc-200">
          <p className="text-xs font-medium text-zinc-500 uppercase tracking-wider">Items</p>
        </div>
        <div className="divide-y divide-zinc-100">
          {order.orderItems?.map((item) => (
            <div key={item.id} className="flex items-center justify-between px-5 py-4">
              <div>
                <p className="text-sm font-medium text-zinc-900">{item.productName}</p>
                <p className="text-xs text-zinc-400 mt-0.5">Qty: {item.quantity}</p>
              </div>
              <span className="text-sm font-semibold text-zinc-900">
                ${item.totalPrice?.toFixed(2) ?? (item.priceAtOrder * item.quantity).toFixed(2)}
              </span>
            </div>
          ))}
        </div>
        <div className="px-5 py-4 bg-zinc-50 border-t border-zinc-200 flex justify-between items-center">
          <span className="text-sm font-medium text-zinc-900">Total</span>
          <span className="text-base font-semibold text-zinc-900">${order.totalPrice?.toFixed(2)}</span>
        </div>
      </div>

      {order.status === 'PENDING' && (
        <Button variant="outline" size="sm"
          onClick={handleCancel} disabled={cancelling}
          className="text-red-600 border-red-200 hover:bg-red-50 hover:text-red-700">
          {cancelling ? 'Cancelling…' : 'Cancel order'}
        </Button>
      )}
    </div>
  )
}
