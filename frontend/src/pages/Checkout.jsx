import { useState, useEffect } from 'react'
import { useNavigate, useSearchParams, Link } from 'react-router-dom'
import { Check, ChevronRight } from 'lucide-react'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import { Separator } from '../components/ui/separator'
import { useAuth } from '../context/AuthContext'
import client from '../api/client'

// ─── Step indicator ────────────────────────────────────────────────────────────
function Steps({ current, steps }) {
  return (
    <div className="flex items-center gap-2 mb-10">
      {steps.map((label, i) => {
        const done    = i < current
        const active  = i === current
        return (
          <div key={i} className="flex items-center gap-2">
            <div className={`flex items-center gap-2 text-sm ${active ? 'text-zinc-900 font-medium' : done ? 'text-zinc-400' : 'text-zinc-300'}`}>
              <span className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-semibold
                ${done  ? 'bg-zinc-900 text-white' :
                  active ? 'border-2 border-zinc-900 text-zinc-900' :
                           'border border-zinc-200 text-zinc-300'}`}>
                {done ? <Check className="w-3 h-3" /> : i + 1}
              </span>
              {label}
            </div>
            {i < steps.length - 1 && <ChevronRight className="w-4 h-4 text-zinc-200" />}
          </div>
        )
      })}
    </div>
  )
}

// ─── Order summary sidebar ─────────────────────────────────────────────────────
function OrderSummary({ cart }) {
  const subtotal = cart.reduce((s, i) => s + i.price * i.quantity, 0)
  return (
    <div className="bg-zinc-50 rounded-xl border border-zinc-200 p-5">
      <p className="text-xs font-semibold text-zinc-500 uppercase tracking-wider mb-4">Order summary</p>
      <div className="space-y-2.5 text-sm mb-4">
        {cart.map((item) => (
          <div key={item.id} className="flex justify-between">
            <span className="text-zinc-600">{item.name} × {item.quantity}</span>
            <span className="font-medium text-zinc-900">${(item.price * item.quantity).toFixed(2)}</span>
          </div>
        ))}
      </div>
      <Separator />
      <div className="flex justify-between font-semibold text-zinc-900 mt-3">
        <span>Total</span>
        <span>${subtotal.toFixed(2)}</span>
      </div>
      <p className="text-xs text-zinc-400 mt-2">Free shipping · Secure checkout</p>
    </div>
  )
}

// ─── Step 1: Auth ──────────────────────────────────────────────────────────────
function StepAuth({ isGuest, onComplete }) {
  const { login } = useAuth()
  const [mode, setMode] = useState(isGuest ? 'guest' : 'login')
  const [guestEmail, setGuestEmail] = useState('')
  const [loginForm, setLoginForm] = useState({ username: '', password: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  async function handleGuest(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      // Auto-generate credentials — guest only needs their email
      const password = crypto.randomUUID()
      const namePart = guestEmail.split('@')[0]
      await client.post('/auth/register', {
        username: guestEmail,
        email: guestEmail,
        password,
        firstName: namePart,
        lastName: 'Guest',
      })
      const { data: token } = await client.post('/auth/login', { username: guestEmail, password })
      login(token)
      onComplete()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not continue. Try a different email.')
    } finally {
      setLoading(false)
    }
  }

  async function handleLogin(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data: token } = await client.post('/auth/login', loginForm)
      login(token)
      onComplete()
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid email or password.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-4">
      {/* Mode toggle */}
      <div className="flex border border-zinc-200 rounded-lg overflow-hidden text-sm">
        {[['guest', 'Continue as guest'], ['login', 'Sign in']].map(([m, label]) => (
          <button key={m} onClick={() => setMode(m)}
            className={`flex-1 py-2.5 font-medium transition-colors ${mode === m ? 'bg-zinc-900 text-white' : 'text-zinc-500 hover:bg-zinc-50'}`}>
            {label}
          </button>
        ))}
      </div>

      {mode === 'guest' ? (
        <form onSubmit={handleGuest} className="space-y-3">
          <div className="space-y-1.5">
            <Label>Email address</Label>
            <Input type="email" placeholder="john@example.com" value={guestEmail}
              onChange={(e) => setGuestEmail(e.target.value)} required />
          </div>
          {error && <p className="text-xs text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2">{error}</p>}
          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? 'Continuing…' : 'Continue to shipping'}
          </Button>
          <p className="text-xs text-zinc-400 text-center">We'll use this to send your order confirmation</p>
        </form>
      ) : (
        <form onSubmit={handleLogin} className="space-y-3">
          <div className="space-y-1.5">
            <Label>Email</Label>
            <Input type="email" placeholder="john@example.com" value={loginForm.username}
              onChange={(e) => setLoginForm(p => ({ ...p, username: e.target.value }))} required />
          </div>
          <div className="space-y-1.5">
            <Label>Password</Label>
            <Input type="password" placeholder="••••••••" value={loginForm.password}
              onChange={(e) => setLoginForm(p => ({ ...p, password: e.target.value }))} required />
          </div>
          {error && <p className="text-xs text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2">{error}</p>}
          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? 'Signing in…' : 'Sign in & continue'}
          </Button>
        </form>
      )}
    </div>
  )
}

// ─── Step 2: Address ───────────────────────────────────────────────────────────
function StepAddress({ onComplete }) {
  const [addresses, setAddresses] = useState([])
  const [selected, setSelected] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ street: '', city: '', state: '', postalCode: '', country: '' })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  function set(f) { return (e) => setForm(p => ({ ...p, [f]: e.target.value })) }

  useEffect(() => {
    client.get('/users/profile').then(({ data }) => {
      const addrs = data.addresses || []
      setAddresses(addrs)
      if (addrs.length > 0) {
        setSelected(addrs[0].id)
        setShowForm(false)
      } else {
        setShowForm(true)
      }
    }).catch(() => setShowForm(true))
  }, [])

  async function saveAndContinue(e) {
    e.preventDefault()
    setSaving(true)
    setError('')
    try {
      const { data: newAddr } = await client.post('/users/address', form)
      onComplete(newAddr.id)
    } catch (err) {
      setError(err.response?.data?.message || 'Could not save address.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="space-y-5">
      {/* Saved addresses */}
      {addresses.length > 0 && !showForm && (
        <>
          <div className="space-y-2">
            {addresses.map((addr) => (
              <label key={addr.id}
                className={`flex items-start gap-3 border rounded-xl p-4 cursor-pointer transition-colors ${
                  selected === addr.id ? 'border-zinc-900 bg-zinc-50' : 'border-zinc-200 hover:border-zinc-300'
                }`}>
                <input type="radio" name="addr" value={addr.id} checked={selected === addr.id}
                  onChange={() => setSelected(addr.id)} className="mt-0.5 accent-zinc-900" />
                <div className="text-sm">
                  <p className="font-medium text-zinc-900">{addr.street}</p>
                  <p className="text-zinc-500">{addr.city}{addr.state ? `, ${addr.state}` : ''} {addr.postalCode} — {addr.country}</p>
                </div>
              </label>
            ))}
          </div>
          <div className="flex items-center gap-3">
            <Button className="flex-1" onClick={() => onComplete(selected)}>Use this address</Button>
            <Button variant="outline" onClick={() => setShowForm(true)}>+ New address</Button>
          </div>
        </>
      )}

      {/* Address form */}
      {showForm && (
        <form onSubmit={saveAndContinue} className="space-y-3">
          <div className="space-y-1.5">
            <Label>Street address</Label>
            <Input placeholder="123 Main St" value={form.street} onChange={set('street')} required />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label>City</Label>
              <Input placeholder="Istanbul" value={form.city} onChange={set('city')} required />
            </div>
            <div className="space-y-1.5">
              <Label>State / Province</Label>
              <Input placeholder="Istanbul" value={form.state} onChange={set('state')} required />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label>Postal code</Label>
              <Input placeholder="34000" value={form.postalCode} onChange={set('postalCode')} required />
            </div>
            <div className="space-y-1.5">
              <Label>Country</Label>
              <Input placeholder="Turkey" value={form.country} onChange={set('country')} required />
            </div>
          </div>
          {error && <p className="text-xs text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2">{error}</p>}
          <div className="flex gap-3">
            {addresses.length > 0 && (
              <Button type="button" variant="outline" onClick={() => setShowForm(false)}>Back</Button>
            )}
            <Button type="submit" className="flex-1" disabled={saving}>
              {saving ? 'Saving…' : 'Save & continue'}
            </Button>
          </div>
        </form>
      )}
    </div>
  )
}

// ─── Step 3: Payment ───────────────────────────────────────────────────────────
function StepPayment({ cart, addressId, onComplete }) {
  const [form, setForm] = useState({ number: '', name: '', expiry: '', cvv: '' })
  const [placing, setPlacing] = useState(false)
  const [error, setError] = useState('')

  function set(f) { return (e) => setForm(p => ({ ...p, [f]: e.target.value })) }

  function formatCard(v) {
    return v.replace(/\D/g, '').slice(0, 16).replace(/(.{4})/g, '$1 ').trim()
  }
  function formatExpiry(v) {
    const d = v.replace(/\D/g, '').slice(0, 4)
    return d.length >= 3 ? `${d.slice(0,2)}/${d.slice(2)}` : d
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setPlacing(true)
    try {
      await client.post('/orders', {
        shippingAddressId: addressId,
        items: cart.map((i) => ({ productId: i.id, quantity: i.quantity })),
      })
      localStorage.removeItem('cart')
      onComplete()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not place order.')
    } finally {
      setPlacing(false)
    }
  }

  const subtotal = cart.reduce((s, i) => s + i.price * i.quantity, 0)

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <div className="space-y-1.5">
        <Label>Card number</Label>
        <Input placeholder="1234 5678 9012 3456" value={form.number}
          onChange={(e) => setForm(p => ({ ...p, number: formatCard(e.target.value) }))}
          maxLength={19} required />
      </div>
      <div className="space-y-1.5">
        <Label>Cardholder name</Label>
        <Input placeholder="John Doe" value={form.name} onChange={set('name')} required />
      </div>
      <div className="grid grid-cols-2 gap-3">
        <div className="space-y-1.5">
          <Label>Expiry date</Label>
          <Input placeholder="MM/YY" value={form.expiry}
            onChange={(e) => setForm(p => ({ ...p, expiry: formatExpiry(e.target.value) }))}
            maxLength={5} required />
        </div>
        <div className="space-y-1.5">
          <Label>CVV</Label>
          <Input placeholder="123" value={form.cvv}
            onChange={(e) => setForm(p => ({ ...p, cvv: e.target.value.replace(/\D/g,'').slice(0,4) }))}
            maxLength={4} required />
        </div>
      </div>

      <div className="bg-zinc-50 rounded-lg border border-zinc-200 px-4 py-3 flex items-center gap-2 text-xs text-zinc-500">
        <svg className="w-4 h-4 text-zinc-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
        </svg>
        Your payment info is encrypted and secure
      </div>

      {error && <p className="text-xs text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2">{error}</p>}

      <Button type="submit" className="w-full" size="lg" disabled={placing}>
        {placing ? 'Placing order…' : `Pay $${subtotal.toFixed(2)}`}
      </Button>
    </form>
  )
}

// ─── Step 4: Confirmation ──────────────────────────────────────────────────────
function StepConfirmation() {
  const navigate = useNavigate()
  return (
    <div className="text-center py-8">
      <div className="w-14 h-14 rounded-full bg-zinc-900 flex items-center justify-center mx-auto mb-5">
        <Check className="w-7 h-7 text-white" />
      </div>
      <h2 className="text-xl font-semibold text-zinc-900 mb-2">Order placed!</h2>
      <p className="text-sm text-zinc-500 mb-8">
        We've received your order and will get it ready soon.
      </p>
      <div className="flex items-center justify-center gap-3">
        <Button onClick={() => navigate('/orders')}>View my orders</Button>
        <Button variant="outline" onClick={() => navigate('/products')}>Continue shopping</Button>
      </div>
    </div>
  )
}

// ─── Main Checkout ─────────────────────────────────────────────────────────────
const STEPS = ['Account', 'Address', 'Payment']

export default function Checkout() {
  const { token } = useAuth()
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const isGuest = searchParams.get('guest') === 'true'

  const [cart] = useState(() => JSON.parse(localStorage.getItem('cart') || '[]'))
  const [step, setStep] = useState(token ? 1 : 0)  // logged in → skip auth step
  const [addressId, setAddressId] = useState(null)
  const [done, setDone] = useState(false)

  useEffect(() => {
    if (cart.length === 0) navigate('/cart')
  }, [])

  if (cart.length === 0) return null

  if (done) return (
    <div className="max-w-lg mx-auto px-4 py-16">
      <StepConfirmation />
    </div>
  )

  // Steps shown: if already logged in, skip account step display
  const visibleSteps = token ? ['Address', 'Payment'] : STEPS
  const visibleStep  = token ? step - 1 : step

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 py-10">
      <h1 className="text-2xl font-semibold text-zinc-900 mb-2">Checkout</h1>
      <Steps current={visibleStep} steps={visibleSteps} />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-10">
        {/* Left: step content */}
        <div className="lg:col-span-2">
          {step === 0 && (
            <StepAuth isGuest={isGuest} onComplete={() => setStep(1)} />
          )}
          {step === 1 && (
            <StepAddress onComplete={(id) => { setAddressId(id); setStep(2) }} />
          )}
          {step === 2 && (
            <StepPayment cart={cart} addressId={addressId} onComplete={() => setDone(true)} />
          )}
        </div>

        {/* Right: summary */}
        <div>
          <OrderSummary cart={cart} />
        </div>
      </div>
    </div>
  )
}
