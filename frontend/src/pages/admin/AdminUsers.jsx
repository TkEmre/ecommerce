import { useEffect, useState } from 'react'
import { Button } from '../../components/ui/button'
import { Badge } from '../../components/ui/badge'
import client from '../../api/client'

export default function AdminUsers() {
  const [users, setUsers] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    client.get('/users/all', { params: { page, size: 15 } })
      .then(({ data }) => { setUsers(data.content); setTotalPages(data.totalPages) })
      .finally(() => setLoading(false))
  }, [page])

  return (
    <div className="p-8">
      <div className="mb-8">
        <h1 className="text-xl font-semibold text-zinc-900">Users</h1>
        <p className="text-sm text-zinc-500 mt-0.5">All registered accounts</p>
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
                {['ID', 'Username', 'Email', 'Role'].map((h) => (
                  <th key={h} className="px-4 py-3 text-xs font-medium text-zinc-500 uppercase tracking-wider text-left">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-100">
              {users.map((user) => (
                <tr key={user.id} className="hover:bg-zinc-50 transition-colors">
                  <td className="px-4 py-3 text-zinc-400 text-xs">#{user.id}</td>
                  <td className="px-4 py-3 font-medium text-zinc-900">{user.username}</td>
                  <td className="px-4 py-3 text-zinc-500">{user.email}</td>
                  <td className="px-4 py-3">
                    <Badge variant={user.role === 'ADMIN' ? 'default' : 'secondary'}>
                      {user.role}
                    </Badge>
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
    </div>
  )
}
