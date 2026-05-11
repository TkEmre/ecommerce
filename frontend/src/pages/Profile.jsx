import { useEffect, useState } from 'react'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import { Separator } from '../components/ui/separator'
import client from '../api/client'

export default function Profile() {
  const [user, setUser] = useState(null)
  const [editMode, setEditMode] = useState(false)
  const [form, setForm] = useState({})
  const [addrForm, setAddrForm] = useState({ street: '', city: '', state: '', postalCode: '', country: '' })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [addingAddr, setAddingAddr] = useState(false)
  const [toast, setToast] = useState('')

  function showToast(msg) { setToast(msg); setTimeout(() => setToast(''), 3000) }

  useEffect(() => {
    client.get('/users/profile').then(({ data }) => {
      setUser(data)
      setForm({ username: data.username, email: data.email })
    }).finally(() => setLoading(false))
  }, [])

  async function saveProfile(e) {
    e.preventDefault()
    setSaving(true)
    try {
      const { data } = await client.put('/users/profile', form)
      setUser(data)
      setEditMode(false)
      showToast('Profile updated')
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to update.')
    } finally {
      setSaving(false)
    }
  }

  async function addAddress(e) {
    e.preventDefault()
    setAddingAddr(true)
    try {
      const { data } = await client.post('/users/address', addrForm)
      setUser((u) => ({ ...u, addresses: [...(u.addresses || []), data] }))
      setAddrForm({ street: '', city: '', country: '', zipCode: '' })
      showToast('Address added')
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to add address.')
    } finally {
      setAddingAddr(false)
    }
  }

  if (loading) return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 py-10 space-y-4 animate-pulse">
      <div className="h-6 bg-zinc-100 rounded w-1/4" />
      <div className="h-40 bg-zinc-100 rounded-xl" />
      <div className="h-56 bg-zinc-100 rounded-xl" />
    </div>
  )

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 py-10">
      <h1 className="text-2xl font-semibold text-zinc-900 mb-8">Profile</h1>

      {toast && (
        <div className="mb-4 text-sm bg-zinc-900 text-zinc-50 px-4 py-3 rounded-lg">
          {toast}
        </div>
      )}

      {/* Account info */}
      <div className="border border-zinc-200 rounded-xl overflow-hidden mb-6">
        <div className="flex items-center justify-between px-5 py-4 bg-zinc-50 border-b border-zinc-200">
          <p className="text-xs font-medium text-zinc-500 uppercase tracking-wider">Account</p>
          <button onClick={() => setEditMode(!editMode)}
            className="text-xs text-zinc-500 hover:text-zinc-900 transition-colors">
            {editMode ? 'Cancel' : 'Edit'}
          </button>
        </div>

        <div className="p-5">
          {editMode ? (
            <form onSubmit={saveProfile} className="space-y-4">
              <div className="space-y-1.5">
                <Label htmlFor="username">Username</Label>
                <Input id="username" value={form.username}
                  onChange={(e) => setForm({ ...form, username: e.target.value })} />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="email">Email</Label>
                <Input id="email" type="email" value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })} />
              </div>
              <Button type="submit" size="sm" disabled={saving}>
                {saving ? 'Saving…' : 'Save changes'}
              </Button>
            </form>
          ) : (
            <div className="divide-y divide-zinc-100">
              {[
                ['Username', user.username],
                ['Email', user.email],
                ['Role', [...(user.roles || [])].map(r => r.toString()).join(', ')],
              ].map(([label, value]) => (
                <div key={label} className="flex justify-between py-3">
                  <span className="text-sm text-zinc-500">{label}</span>
                  <span className="text-sm font-medium text-zinc-900">{value}</span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Addresses */}
      <div className="border border-zinc-200 rounded-xl overflow-hidden">
        <div className="px-5 py-4 bg-zinc-50 border-b border-zinc-200">
          <p className="text-xs font-medium text-zinc-500 uppercase tracking-wider">Shipping addresses</p>
        </div>

        <div className="p-5">
          {user.addresses?.length > 0 ? (
            <div className="space-y-2 mb-6">
              {user.addresses.map((addr) => (
                <div key={addr.id} className="bg-zinc-50 rounded-lg px-4 py-3 text-sm">
                  <p className="font-medium text-zinc-900">{addr.street}</p>
                  <p className="text-zinc-500">{addr.city}{addr.state ? `, ${addr.state}` : ''} {addr.postalCode} — {addr.country}</p>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-sm text-zinc-400 mb-6">No addresses saved yet.</p>
          )}

          <Separator className="mb-5" />

          <p className="text-xs font-medium text-zinc-500 uppercase tracking-wider mb-4">Add address</p>
          <form onSubmit={addAddress} className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div className="sm:col-span-2 space-y-1.5">
              <Label>Street</Label>
              <Input placeholder="123 Main St" value={addrForm.street}
                onChange={(e) => setAddrForm({ ...addrForm, street: e.target.value })} required />
            </div>
            <div className="space-y-1.5">
              <Label>City</Label>
              <Input placeholder="Istanbul" value={addrForm.city}
                onChange={(e) => setAddrForm({ ...addrForm, city: e.target.value })} required />
            </div>
            <div className="space-y-1.5">
              <Label>State / Province</Label>
              <Input placeholder="Istanbul" value={addrForm.state}
                onChange={(e) => setAddrForm({ ...addrForm, state: e.target.value })} required />
            </div>
            <div className="space-y-1.5">
              <Label>Postal Code</Label>
              <Input placeholder="34000" value={addrForm.postalCode}
                onChange={(e) => setAddrForm({ ...addrForm, postalCode: e.target.value })} required />
            </div>
            <div className="space-y-1.5">
              <Label>Country</Label>
              <Input placeholder="Turkey" value={addrForm.country}
                onChange={(e) => setAddrForm({ ...addrForm, country: e.target.value })} required />
            </div>
            <div className="sm:col-span-2">
              <Button type="submit" variant="secondary" size="sm" disabled={addingAddr}>
                {addingAddr ? 'Adding…' : 'Add address'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
