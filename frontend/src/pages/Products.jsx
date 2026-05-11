import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Search, SlidersHorizontal, ShoppingBag } from 'lucide-react'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Badge } from '../components/ui/badge'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../components/ui/select'
import client from '../api/client'

function ProductSkeleton() {
  return (
    <div className="animate-pulse">
      <div className="bg-zinc-100 rounded-xl aspect-square mb-3" />
      <div className="h-3 bg-zinc-100 rounded w-1/3 mb-2" />
      <div className="h-4 bg-zinc-100 rounded w-2/3 mb-2" />
      <div className="h-4 bg-zinc-100 rounded w-1/4" />
    </div>
  )
}

export default function Products() {
  const [products, setProducts] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [category, setCategory] = useState('all')
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [addedId, setAddedId] = useState(null)

  useEffect(() => { fetchProducts() }, [page, category])

  async function fetchProducts() {
    setLoading(true)
    try {
      const url = category !== 'all' ? `/products/category/${category}` : '/products'
      const { data } = await client.get(url, { params: { page, size: 12 } })
      setProducts(data.content)
      setTotalPages(data.totalPages)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  function addToCart(product) {
    const cart = JSON.parse(localStorage.getItem('cart') || '[]')
    const existing = cart.find((i) => i.id === product.id)
    if (existing) existing.quantity += 1
    else cart.push({ ...product, quantity: 1 })
    localStorage.setItem('cart', JSON.stringify(cart))
    setAddedId(product.id)
    setTimeout(() => setAddedId(null), 1500)
  }

  const filtered = products.filter((p) =>
    p.name.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 py-10">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-2xl font-semibold text-zinc-900 mb-1">Products</h1>
        <p className="text-sm text-zinc-500">Browse our collection</p>
      </div>

      {/* Filters */}
      <div className="flex flex-col sm:flex-row gap-3 mb-8">
        <div className="relative flex-1 max-w-xs">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-zinc-400" />
          <Input
            placeholder="Search products…"
            className="pl-9"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <Select value={category} onValueChange={(v) => { setCategory(v); setPage(0) }}>
          <SelectTrigger className="w-44">
            <SlidersHorizontal className="w-3.5 h-3.5 mr-2 text-zinc-400" />
            <SelectValue placeholder="Category" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All categories</SelectItem>
            <SelectItem value="electronics">Electronics</SelectItem>
            <SelectItem value="clothing">Clothing</SelectItem>
            <SelectItem value="books">Books</SelectItem>
            <SelectItem value="home">Home</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Grid */}
      {loading ? (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {Array.from({ length: 12 }).map((_, i) => <ProductSkeleton key={i} />)}
        </div>
      ) : filtered.length === 0 ? (
        <div className="text-center py-24 text-zinc-400 text-sm">No products found.</div>
      ) : (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {filtered.map((product) => (
            <div key={product.id} className="group">
              {/* Image */}
              <Link to={`/products/${product.id}`}>
                <div className="bg-zinc-50 rounded-xl aspect-square flex items-center justify-center mb-3 overflow-hidden border border-zinc-100 group-hover:border-zinc-300 transition-colors">
                  {product.imageUrl ? (
                    <img src={product.imageUrl} alt={product.name}
                      className="w-full h-full object-cover" />
                  ) : (
                    <ShoppingBag className="w-10 h-10 text-zinc-200" />
                  )}
                </div>
              </Link>

              {/* Info */}
              <div>
                <p className="text-xs text-zinc-400 uppercase tracking-wider mb-1">{product.category}</p>
                <Link to={`/products/${product.id}`}>
                  <p className="text-sm font-medium text-zinc-900 hover:text-zinc-600 transition-colors line-clamp-1 mb-2">
                    {product.name}
                  </p>
                </Link>
                <div className="flex items-center justify-between">
                  <span className="text-sm font-semibold text-zinc-900">${product.price?.toFixed(2)}</span>
                  {product.stock === 0 ? (
                    <Badge variant="secondary" className="text-xs">Sold out</Badge>
                  ) : (
                    <button
                      onClick={() => addToCart(product)}
                      className={`text-xs font-medium transition-colors ${
                        addedId === product.id
                          ? 'text-emerald-600'
                          : 'text-zinc-500 hover:text-zinc-900'
                      }`}
                    >
                      {addedId === product.id ? 'Added ✓' : '+ Add'}
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-3 mt-12">
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
