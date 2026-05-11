import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { ArrowRight } from 'lucide-react'
import { Button } from '../components/ui/button'
import { Badge } from '../components/ui/badge'
import client from '../api/client'

const STATUS_VARIANT = {
  PENDING:    'warning',
  PROCESSING: 'info',
  SHIPPED:    'info',
  DELIVERED:  'success',
  CANCELED:   'destructive',
  RETURNED:   'secondary',
}

export default function Orders() {
  const [orders, setOrders] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    client.get('/orders', { params: { page, size: 10 } })
      .then(({ data }) => { setOrders(data.content); setTotalPages(data.totalPages) })
      .finally(() => setLoading(false))
  }, [page])

  if (loading) return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 py-10 space-y-3">
      {Array.from({ length: 4 }).map((_, i) => (
        <div key={i} className="bg-zinc-100 rounded-xl h-20 animate-pulse" />
      ))}
    </div>
  )

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 py-10">
      <div className="mb-8">
        <h1 className="text-2xl font-semibold text-zinc-900">Orders</h1>
        <p className="text-sm text-zinc-500 mt-1">Track and manage your orders</p>
      </div>

      {orders.length === 0 ? (
        <div className="text-center py-20">
          <p className="text-zinc-400 text-sm mb-4">No orders yet</p>
          <Button variant="outline" asChild>
            <Link to="/products">Start shopping</Link>
          </Button>
        </div>
      ) : (
        <div className="divide-y divide-zinc-100">
          {orders.map((order) => (
            <Link key={order.id} to={`/orders/${order.id}`}
              className="flex items-center justify-between py-5 group">
              <div>
                <div className="flex items-center gap-2.5 mb-1">
                  <span className="text-sm font-medium text-zinc-900">Order #{order.id}</span>
                  <Badge variant={STATUS_VARIANT[order.status] || 'secondary'}>
                    {order.status}
                  </Badge>
                </div>
                <p className="text-xs text-zinc-400">
                  {new Date(order.orderDate).toLocaleDateString('en-US', {
                    year: 'numeric', month: 'long', day: 'numeric',
                  })}
                  {' · '}
                  {order.orderItems?.length} item{order.orderItems?.length !== 1 ? 's' : ''}
                </p>
              </div>
              <div className="flex items-center gap-3">
                <span className="text-sm font-semibold text-zinc-900">${order.totalPrice?.toFixed(2)}</span>
                <ArrowRight className="w-4 h-4 text-zinc-300 group-hover:text-zinc-900 transition-colors" />
              </div>
            </Link>
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-3 mt-8">
          <Button variant="outline" size="sm" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>
            Previous
          </Button>
          <span className="text-sm text-zinc-500">{page + 1} / {totalPages}</span>
          <Button variant="outline" size="sm" onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))} disabled={page >= totalPages - 1}>
            Next
          </Button>
        </div>
      )}
    </div>
  )
}
