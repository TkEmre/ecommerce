import { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { ArrowLeft, ShoppingBag, Minus, Plus } from 'lucide-react'
import { Button } from '../components/ui/button'
import { Badge } from '../components/ui/badge'
import { Separator } from '../components/ui/separator'
import client from '../api/client'

export default function ProductDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const [quantity, setQuantity] = useState(1)
  const [added, setAdded] = useState(false)

  useEffect(() => {
    client.get(`/products/${id}`)
      .then(({ data }) => setProduct(data))
      .catch(() => navigate('/products'))
      .finally(() => setLoading(false))
  }, [id])

  function addToCart() {
    const cart = JSON.parse(localStorage.getItem('cart') || '[]')
    const existing = cart.find((i) => i.id === product.id)
    if (existing) existing.quantity += quantity
    else cart.push({ ...product, quantity })
    localStorage.setItem('cart', JSON.stringify(cart))
    setAdded(true)
    setTimeout(() => setAdded(false), 2000)
  }

  if (loading) return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 py-10">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-12 animate-pulse">
        <div className="bg-zinc-100 rounded-2xl aspect-square" />
        <div className="space-y-4 pt-4">
          <div className="h-3 bg-zinc-100 rounded w-1/4" />
          <div className="h-8 bg-zinc-100 rounded w-3/4" />
          <div className="h-6 bg-zinc-100 rounded w-1/5" />
          <div className="h-20 bg-zinc-100 rounded" />
        </div>
      </div>
    </div>
  )

  if (!product) return null

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 py-10">
      <button onClick={() => navigate('/products')}
        className="flex items-center gap-1.5 text-sm text-zinc-500 hover:text-zinc-900 transition-colors mb-8">
        <ArrowLeft className="w-4 h-4" />
        Back
      </button>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
        {/* Image */}
        <div className="bg-zinc-50 rounded-2xl aspect-square flex items-center justify-center border border-zinc-100 overflow-hidden">
          {product.imageUrl ? (
            <img src={product.imageUrl} alt={product.name} className="w-full h-full object-cover" />
          ) : (
            <ShoppingBag className="w-20 h-20 text-zinc-200" />
          )}
        </div>

        {/* Info */}
        <div className="flex flex-col gap-6 pt-2">
          <div>
            <p className="text-xs text-zinc-400 uppercase tracking-wider mb-2">{product.category}</p>
            <h1 className="text-3xl font-semibold text-zinc-900 tracking-tight">{product.name}</h1>
          </div>

          <div className="text-3xl font-semibold text-zinc-900">${product.price?.toFixed(2)}</div>

          <Separator />

          <p className="text-sm text-zinc-500 leading-relaxed">
            {product.description || 'No description available.'}
          </p>

          {product.stock > 0 ? (
            <p className="text-sm text-emerald-600 font-medium">{product.stock} in stock</p>
          ) : (
            <Badge variant="secondary">Out of stock</Badge>
          )}

          {product.stock > 0 && (
            <div className="flex items-center gap-3">
              {/* Quantity */}
              <div className="flex items-center border border-zinc-200 rounded-md">
                <button onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                  className="p-2 text-zinc-500 hover:text-zinc-900 transition-colors">
                  <Minus className="w-3.5 h-3.5" />
                </button>
                <span className="w-10 text-center text-sm font-medium">{quantity}</span>
                <button onClick={() => setQuantity((q) => Math.min(product.stock, q + 1))}
                  className="p-2 text-zinc-500 hover:text-zinc-900 transition-colors">
                  <Plus className="w-3.5 h-3.5" />
                </button>
              </div>

              <Button onClick={addToCart} className="flex-1">
                {added ? 'Added to cart ✓' : 'Add to cart'}
              </Button>
            </div>
          )}

          {added && (
            <div className="flex items-center justify-between text-sm text-zinc-600 bg-zinc-50 rounded-lg px-4 py-3">
              <span>Item added to cart</span>
              <Link to="/cart" className="font-medium text-zinc-900 hover:underline underline-offset-4">
                View cart →
              </Link>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
