import { useEffect, useState } from 'react'
import { Button } from '../../components/ui/button'
import { Badge } from '../../components/ui/badge'
import { Label } from '../../components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../../components/ui/select'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from '../../components/ui/dialog'
import client from '../../api/client'

const STATUSES = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELED', 'RETURNED']
const STATUS_VARIANT = {
  PENDING:    'warning',
  PROCESSING: 'info',
  SHIPPED:    'info',
  DELIVERED:  'success',
  CANCELED:   'destructive',
  RETURNED:   'secondary',
}

export default function AdminOrders() {
  const [orders, setOrders] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)
  const [statusModal, setStatusModal] = useState(null)
  const [newStatus, setNewStatus] = useState('')

  useEffect(() => { fetchOrders() }, [page])

  async function fetchOrders() {
    setLoading(true)
    try {
      const { data } = await client.get('/orders', { params: { page, size: 15 } })
      setOrders(data.content)
      setTotalPages(data.totalPages)
    } finally { setLoading(false) }
  }

  async function handleStatusUpdate(e) {
    e.preventDefault()
    try {
      await client.put(`/orders/${statusModal.id}/status`, { newStatus: newStatus })
      setStatusModal(null)
      fetchOrders()
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to update status.')
    }
  }

  return (
    <div className="p-8">
      <div className="mb-8">
        <h1 className="text-xl font-semibold text-zinc-900">Orders</h1>
        <p className="text-sm text-zinc-500 mt-0.5">Manage and update order statuses</p>
      </div>

      {loading ? (
        <div className="space-y-2">
          {Array.from({ length: 6 }).map((_, i) => <div key={i} className="bg-zinc-100 rounded-lg h-12 animate-pulse" />)}
        </div>
      ) : (
        <div className="border border-zinc-200 rounded-xl overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-zinc-200 bg-zinc-50">
                {['Order', 'Customer', 'Date', 'Total', 'Status', ''].map((h) => (
                  <th key={h} className={`px-4 py-3 text-xs font-medium text-zinc-500 uppercase tracking-wider ${h === '' ? 'text-right' : 'text-left'}`}>
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-100">
              {orders.map((order) => (
                <tr key={order.id} className="hover:bg-zinc-50 transition-colors">
                  <td className="px-4 py-3 font-medium text-zinc-900">#{order.id}</td>
                  <td className="px-4 py-3 text-zinc-500">{order.username || '—'}</td>
                  <td className="px-4 py-3 text-zinc-500">
                    {new Date(order.orderDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                  </td>
                  <td className="px-4 py-3 font-medium text-zinc-900">${order.totalAmount?.toFixed(2)}</td>
                  <td className="px-4 py-3">
                    <Badge variant={STATUS_VARIANT[order.status] || 'secondary'}>{order.status}</Badge>
                  </td>
                  <td className="px-4 py-3 text-right">
                    <Button variant="ghost" size="sm"
                      onClick={() => { setStatusModal(order); setNewStatus(order.status) }}>
                      Update
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-3 mt-6">
          <Button variant="outline" size="sm" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>Previous</Button>
          <span className="text-sm text-zinc-500">{page + 1} / {totalPages}</span>
          <Button variant="outline" size="sm" onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))} disabled={page >= totalPages - 1}>Next</Button>
        </div>
      )}

      <Dialog open={!!statusModal} onOpenChange={(open) => !open && setStatusModal(null)}>
        <DialogContent className="max-w-sm">
          <DialogHeader>
            <DialogTitle>Update status</DialogTitle>
            <DialogDescription>Order #{statusModal?.id}</DialogDescription>
          </DialogHeader>
          <form onSubmit={handleStatusUpdate} className="space-y-4">
            <div className="space-y-1.5">
              <Label>Status</Label>
              <Select value={newStatus} onValueChange={setNewStatus}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  {STATUSES.map((s) => <SelectItem key={s} value={s}>{s}</SelectItem>)}
                </SelectContent>
              </Select>
            </div>
            <div className="flex gap-3">
              <Button type="button" variant="outline" className="flex-1" onClick={() => setStatusModal(null)}>Cancel</Button>
              <Button type="submit" className="flex-1">Save</Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  )
}
