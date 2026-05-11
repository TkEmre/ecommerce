import { useEffect, useState, useRef } from 'react'
import { Plus, Pencil, Trash2, Boxes, ImagePlus } from 'lucide-react'
import { Button } from '../../components/ui/button'
import { Input } from '../../components/ui/input'
import { Label } from '../../components/ui/label'
import { Badge } from '../../components/ui/badge'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from '../../components/ui/dialog'
import client from '../../api/client'

const EMPTY = { name: '', price: '', category: '', stock: '' }

async function uploadImage(productId, file) {
  const fd = new FormData()
  fd.append('file', file)
  const { data } = await import('../../api/client').then(m => m.default.post(`/products/${productId}/image`, fd, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }))
  return data
}

export default function AdminProducts() {
  const [products, setProducts] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [imageFile, setImageFile] = useState(null)
  const [imageModal, setImageModal] = useState(null)
  const [saving, setSaving] = useState(false)
  const [stockModal, setStockModal] = useState(null)
  const [stockQty, setStockQty] = useState('')
  const fileRef = useRef(null)

  useEffect(() => { fetchProducts() }, [page])

  async function fetchProducts() {
    setLoading(true)
    try {
      const { data } = await client.get('/products', { params: { page, size: 10 } })
      setProducts(data.content)
      setTotalPages(data.totalPages)
    } finally { setLoading(false) }
  }

  function openCreate() { setForm(EMPTY); setImageFile(null); setModal('create') }
  function openEdit(p) {
    setForm({ name: p.name, price: p.price, category: p.category, stock: p.stock })
    setImageFile(null)
    setModal(p)
  }

  async function handleSave(e) {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = { ...form, price: Number(form.price), stock: Number(form.stock) }
      let savedProduct
      if (modal === 'create') {
        const { data } = await client.post('/products', payload)
        savedProduct = data
      } else {
        const { data } = await client.put(`/products/${modal.id}`, { ...payload, active: true })
        savedProduct = data
      }
      if (imageFile) {
        const fd = new FormData()
        fd.append('file', imageFile)
        await client.post(`/products/${savedProduct.id}/image`, fd, {
          headers: { 'Content-Type': 'multipart/form-data' },
        })
      }
      setModal(null)
      fetchProducts()
    } catch (err) {
      alert(err.response?.data?.message || 'Error saving product.')
    } finally { setSaving(false) }
  }

  async function handleDelete(id) {
    if (!confirm('Delete this product?')) return
    await client.delete(`/products/${id}`)
    fetchProducts()
  }

  async function handleStock(e) {
    e.preventDefault()
    try {
      await client.patch(`/products/${stockModal.id}/stock`, null, { params: { quantity: Number(stockQty) } })
      setStockModal(null)
      fetchProducts()
    } catch (err) {
      alert(err.response?.data?.message || 'Error updating stock.')
    }
  }

  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-xl font-semibold text-zinc-900">Products</h1>
          <p className="text-sm text-zinc-500 mt-0.5">Manage your product catalog</p>
        </div>
        <Button size="sm" onClick={openCreate}>
          <Plus className="w-4 h-4" />
          Add product
        </Button>
      </div>

      {loading ? (
        <div className="space-y-2">
          {Array.from({ length: 5 }).map((_, i) => (
            <div key={i} className="bg-zinc-100 rounded-lg h-12 animate-pulse" />
          ))}
        </div>
      ) : (
        <div className="border border-zinc-200 rounded-xl overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-zinc-200 bg-zinc-50">
                {['', 'Name', 'Category', 'Price', 'Stock', ''].map((h, i) => (
                  <th key={i} className={`px-4 py-3 text-xs font-medium text-zinc-500 uppercase tracking-wider ${h === '' && i > 0 ? 'text-right' : 'text-left'}`}>
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-100">
              {products.map((p) => (
                <tr key={p.id} className="hover:bg-zinc-50 transition-colors">
                  <td className="px-4 py-3">
                    <div className="w-9 h-9 rounded-lg bg-zinc-100 overflow-hidden flex items-center justify-center flex-shrink-0">
                      {p.imageUrl ? (
                        <img src={p.imageUrl} alt={p.name} className="w-full h-full object-cover" />
                      ) : (
                        <ImagePlus className="w-4 h-4 text-zinc-300" />
                      )}
                    </div>
                  </td>
                  <td className="px-4 py-3 font-medium text-zinc-900">{p.name}</td>
                  <td className="px-4 py-3 text-zinc-500">{p.category}</td>
                  <td className="px-4 py-3 text-zinc-900">${p.price?.toFixed(2)}</td>
                  <td className="px-4 py-3">
                    <Badge variant={p.stock > 0 ? 'success' : 'destructive'}>
                      {p.stock}
                    </Badge>
                  </td>
                  <td className="px-4 py-3 text-right">
                    <div className="flex items-center justify-end gap-1">
                      <Button variant="ghost" size="icon" onClick={() => { setStockModal(p); setStockQty(p.stock) }}>
                        <Boxes className="w-4 h-4" />
                      </Button>
                      <Button variant="ghost" size="icon" onClick={() => openEdit(p)}>
                        <Pencil className="w-4 h-4" />
                      </Button>
                      <Button variant="ghost" size="icon" onClick={() => handleDelete(p.id)}
                        className="text-red-400 hover:text-red-600 hover:bg-red-50">
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
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

      {/* Create / Edit */}
      <Dialog open={modal !== null} onOpenChange={(open) => !open && setModal(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{modal === 'create' ? 'Add product' : 'Edit product'}</DialogTitle>
            <DialogDescription>Fill in the product details below.</DialogDescription>
          </DialogHeader>
          <form onSubmit={handleSave} className="space-y-4">
            {[
              ['name', 'Name', 'text'],
              ['category', 'Category', 'text'],
              ['price', 'Price', 'number'],
              ['stock', 'Stock quantity', 'number'],
            ].map(([field, label, type]) => (
              <div key={field} className="space-y-1.5">
                <Label>{label}</Label>
                <Input type={type} required value={form[field]}
                  onChange={(e) => setForm({ ...form, [field]: e.target.value })} />
              </div>
            ))}
            <div className="space-y-1.5">
              <Label>Product image <span className="text-zinc-400 font-normal">(optional)</span></Label>
              <label className="flex items-center gap-3 border border-zinc-200 rounded-lg px-3 py-2.5 cursor-pointer hover:bg-zinc-50 transition-colors">
                <ImagePlus className="w-4 h-4 text-zinc-400 flex-shrink-0" />
                <span className="text-sm text-zinc-500 truncate">
                  {imageFile ? imageFile.name : 'Choose a file…'}
                </span>
                <input ref={fileRef} type="file" accept="image/*" className="hidden"
                  onChange={(e) => setImageFile(e.target.files[0] || null)} />
              </label>
            </div>
            <div className="flex gap-3 pt-2">
              <Button type="button" variant="outline" className="flex-1" onClick={() => setModal(null)}>Cancel</Button>
              <Button type="submit" className="flex-1" disabled={saving}>
                {saving ? 'Saving…' : 'Save'}
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* Stock */}
      <Dialog open={!!stockModal} onOpenChange={(open) => !open && setStockModal(null)}>
        <DialogContent className="max-w-sm">
          <DialogHeader>
            <DialogTitle>Update stock</DialogTitle>
            <DialogDescription>{stockModal?.name}</DialogDescription>
          </DialogHeader>
          <form onSubmit={handleStock} className="space-y-4">
            <div className="space-y-1.5">
              <Label>New quantity</Label>
              <Input type="number" min="0" required value={stockQty}
                onChange={(e) => setStockQty(e.target.value)} />
            </div>
            <div className="flex gap-3">
              <Button type="button" variant="outline" className="flex-1" onClick={() => setStockModal(null)}>Cancel</Button>
              <Button type="submit" className="flex-1">Update</Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  )
}
