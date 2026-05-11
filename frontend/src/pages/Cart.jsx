import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Minus, Plus, Trash2, ShoppingBag } from 'lucide-react'
import { Button } from '../components/ui/button'
import { Separator } from '../components/ui/separator'
import { useAuth } from '../context/AuthContext'

export default function Cart() {
  const [cart, setCart] = useState([])
  const { token } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    setCart(JSON.parse(localStorage.getItem('cart') || '[]'))
  }, [])

  function update(id, qty) {
    const updated = cart.map((i) => i.id === id ? { ...i, quantity: Math.max(1, qty) } : i)
    setCart(updated)
    localStorage.setItem('cart', JSON.stringify(updated))
  }

  function remove(id) {
    const updated = cart.filter((i) => i.id !== id)
    setCart(updated)
    localStorage.setItem('cart', JSON.stringify(updated))
  }

  const subtotal = cart.reduce((s, i) => s + i.price * i.quantity, 0)
  const itemCount = cart.reduce((s, i) => s + i.quantity, 0)

  if (cart.length === 0) return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 py-24 text-center">
      <ShoppingBag className="w-12 h-12 text-zinc-200 mx-auto mb-4" />
      <h2 className="text-lg font-semibold text-zinc-900 mb-1">Your cart is empty</h2>
      <p className="text-sm text-zinc-500 mb-6">Add some products to get started</p>
      <Button asChild><Link to="/products">Browse products</Link></Button>
    </div>
  )

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 py-10">
      <h1 className="text-2xl font-semibold text-zinc-900 mb-8">Cart <span className="text-zinc-400 font-normal text-lg">({itemCount})</span></h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Items */}
        <div className="lg:col-span-2 space-y-px">
          {cart.map((item, idx) => (
            <div key={item.id}>
              {idx > 0 && <Separator />}
              <div className="flex gap-4 py-5">
                <div className="bg-zinc-50 rounded-lg w-16 h-16 flex-shrink-0 flex items-center justify-center border border-zinc-100">
                  <ShoppingBag className="w-6 h-6 text-zinc-300" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-zinc-900 truncate">{item.name}</p>
                  <p className="text-xs text-zinc-400 mt-0.5">{item.category}</p>
                  <div className="flex items-center justify-between mt-3">
                    <div className="flex items-center border border-zinc-200 rounded-md">
                      <button onClick={() => update(item.id, item.quantity - 1)}
                        className="p-1.5 text-zinc-400 hover:text-zinc-900 transition-colors">
                        <Minus className="w-3 h-3" />
                      </button>
                      <span className="w-8 text-center text-xs font-medium">{item.quantity}</span>
                      <button onClick={() => update(item.id, item.quantity + 1)}
                        className="p-1.5 text-zinc-400 hover:text-zinc-900 transition-colors">
                        <Plus className="w-3 h-3" />
                      </button>
                    </div>
                    <div className="flex items-center gap-3">
                      <span className="text-sm font-semibold text-zinc-900">${(item.price * item.quantity).toFixed(2)}</span>
                      <button onClick={() => remove(item.id)}
                        className="text-zinc-300 hover:text-red-500 transition-colors">
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Summary */}
        <div>
          <div className="bg-zinc-50 rounded-xl border border-zinc-200 p-5 sticky top-20">
            <h2 className="font-semibold text-zinc-900 mb-4">Summary</h2>
            <div className="space-y-2.5 text-sm">
              <div className="flex justify-between text-zinc-600">
                <span>Subtotal</span>
                <span>${subtotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-zinc-600">
                <span>Shipping</span>
                <span className="text-emerald-600">Free</span>
              </div>
              <Separator />
              <div className="flex justify-between font-semibold text-zinc-900 pt-1">
                <span>Total</span>
                <span>${subtotal.toFixed(2)}</span>
              </div>
            </div>
            <Button className="w-full mt-5" onClick={() => navigate('/checkout')}>
              {token ? 'Checkout' : 'Continue as member'}
            </Button>
            {!token && (
              <Button variant="outline" className="w-full mt-2"
                onClick={() => navigate('/checkout?guest=true')}>
                Continue as guest
              </Button>
            )}
            <p className="text-xs text-zinc-400 text-center mt-3">Free shipping on all orders</p>
          </div>
        </div>
      </div>
    </div>
  )
}
