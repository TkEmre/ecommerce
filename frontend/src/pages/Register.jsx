import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ShoppingBag, Store } from 'lucide-react'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../components/ui/card'
import client from '../api/client'

export default function Register() {
  const navigate = useNavigate()
  const [accountType, setAccountType] = useState('buyer') // 'buyer' | 'seller'
  const [form, setForm] = useState({ firstName: '', lastName: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  function set(field) {
    return (e) => setForm((f) => ({ ...f, [field]: e.target.value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await client.post('/auth/register', {
        username: form.email,
        password: form.password,
        email: form.email,
        firstName: form.firstName,
        lastName: form.lastName,
        roles: accountType === 'seller' ? ['SELLER'] : [],
      })
      navigate('/login')
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-zinc-50 flex items-center justify-center px-4">
      <div className="w-full max-w-sm">
        <div className="text-center mb-8">
          <Link to="/" className="text-xl font-semibold tracking-tight text-zinc-900">storé</Link>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Create an account</CardTitle>
            <CardDescription>Choose how you'd like to use storé</CardDescription>
          </CardHeader>
          <CardContent>
            {/* Account type toggle */}
            <div className="grid grid-cols-2 gap-3 mb-6">
              <button
                type="button"
                onClick={() => setAccountType('buyer')}
                className={`flex flex-col items-center gap-2 rounded-xl border p-4 transition-all text-left ${
                  accountType === 'buyer'
                    ? 'border-zinc-900 bg-zinc-900 text-white'
                    : 'border-zinc-200 hover:border-zinc-300 text-zinc-600'
                }`}
              >
                <ShoppingBag className="w-5 h-5" />
                <div>
                  <p className="text-sm font-medium">Shopping</p>
                  <p className={`text-xs mt-0.5 ${accountType === 'buyer' ? 'text-zinc-300' : 'text-zinc-400'}`}>
                    Buy products
                  </p>
                </div>
              </button>
              <button
                type="button"
                onClick={() => setAccountType('seller')}
                className={`flex flex-col items-center gap-2 rounded-xl border p-4 transition-all text-left ${
                  accountType === 'seller'
                    ? 'border-zinc-900 bg-zinc-900 text-white'
                    : 'border-zinc-200 hover:border-zinc-300 text-zinc-600'
                }`}
              >
                <Store className="w-5 h-5" />
                <div>
                  <p className="text-sm font-medium">Selling</p>
                  <p className={`text-xs mt-0.5 ${accountType === 'seller' ? 'text-zinc-300' : 'text-zinc-400'}`}>
                    List & sell products
                  </p>
                </div>
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label htmlFor="firstName">First name</Label>
                  <Input id="firstName" placeholder="John" value={form.firstName} onChange={set('firstName')} required />
                </div>
                <div className="space-y-1.5">
                  <Label htmlFor="lastName">Last name</Label>
                  <Input id="lastName" placeholder="Doe" value={form.lastName} onChange={set('lastName')} required />
                </div>
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="email">Email</Label>
                <Input id="email" type="email" placeholder="john@example.com" value={form.email} onChange={set('email')} required />
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="password">Password</Label>
                <Input id="password" type="password" placeholder="Min. 6 characters" value={form.password} onChange={set('password')} required minLength={6} />
              </div>

              {error && (
                <p className="text-xs text-red-600 bg-red-50 border border-red-200 rounded-md px-3 py-2">{error}</p>
              )}

              <Button type="submit" className="w-full" disabled={loading}>
                {loading ? 'Creating account…' : accountType === 'seller' ? 'Create seller account' : 'Create account'}
              </Button>

              <p className="text-center text-sm text-zinc-500">
                Already have an account?{' '}
                <Link to="/login" className="text-zinc-900 font-medium hover:underline underline-offset-4">Sign in</Link>
              </p>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
