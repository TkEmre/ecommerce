import { Link, useNavigate, useLocation } from 'react-router-dom'
import { ShoppingBag, User, Menu, X } from 'lucide-react'
import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { Button } from './ui/button'
import { Separator } from './ui/separator'
import client from '../api/client'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [mobileOpen, setMobileOpen] = useState(false)

  const cartCount = JSON.parse(localStorage.getItem('cart') || '[]')
    .reduce((s, i) => s + i.quantity, 0)

  async function handleLogout() {
    try { await client.post('/auth/logout') } catch {}
    logout()
    navigate('/login')
  }

  const navLinks = [
    { to: '/products', label: 'Products' },
    ...(user ? [
      { to: '/orders', label: 'Orders' },
    ] : []),
    ...(user?.isAdmin ? [
      { to: '/admin/products', label: 'Admin' },
    ] : user?.isSeller ? [
      { to: '/seller/products', label: 'Dashboard' },
    ] : []),
  ]

  return (
    <header className="sticky top-0 z-50 bg-white border-b border-zinc-200">
      <div className="max-w-6xl mx-auto px-4 sm:px-6">
        <div className="flex items-center justify-between h-14">

          {/* Logo */}
          <Link to="/" className="font-semibold text-zinc-900 tracking-tight text-base">
            storé
          </Link>

          {/* Desktop nav */}
          <nav className="hidden md:flex items-center gap-6">
            {navLinks.map(({ to, label }) => (
              <Link
                key={to}
                to={to}
                className={`text-sm transition-colors ${
                  location.pathname.startsWith(to)
                    ? 'text-zinc-900 font-medium'
                    : 'text-zinc-500 hover:text-zinc-900'
                }`}
              >
                {label}
              </Link>
            ))}
          </nav>

          {/* Right */}
          <div className="flex items-center gap-2">
            <Link to="/cart" className="relative p-2 text-zinc-500 hover:text-zinc-900 transition-colors">
              <ShoppingBag className="w-5 h-5" />
              {cartCount > 0 && (
                <span className="absolute -top-0.5 -right-0.5 bg-zinc-900 text-white text-[10px] font-bold w-4 h-4 rounded-full flex items-center justify-center">
                  {cartCount}
                </span>
              )}
            </Link>

            {user ? (
              <>
                <Link to="/profile" className="p-2 text-zinc-500 hover:text-zinc-900 transition-colors hidden md:block">
                  <User className="w-5 h-5" />
                </Link>
                <Button variant="ghost" size="sm" onClick={handleLogout} className="hidden md:inline-flex text-zinc-500">
                  Sign out
                </Button>
              </>
            ) : (
              <div className="hidden md:flex items-center gap-2">
                <Button variant="ghost" size="sm" asChild>
                  <Link to="/login">Sign in</Link>
                </Button>
                <Button size="sm" asChild>
                  <Link to="/register">Get started</Link>
                </Button>
              </div>
            )}

            {/* Mobile menu toggle */}
            <button
              className="md:hidden p-2 text-zinc-500 hover:text-zinc-900"
              onClick={() => setMobileOpen(!mobileOpen)}
            >
              {mobileOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile menu */}
      {mobileOpen && (
        <div className="md:hidden border-t border-zinc-200 bg-white px-4 py-4 space-y-1">
          {navLinks.map(({ to, label }) => (
            <Link
              key={to}
              to={to}
              onClick={() => setMobileOpen(false)}
              className="block py-2 text-sm text-zinc-600 hover:text-zinc-900"
            >
              {label}
            </Link>
          ))}
          <Separator className="my-2" />
          {user ? (
            <>
              <Link to="/profile" onClick={() => setMobileOpen(false)} className="block py-2 text-sm text-zinc-600">Profile</Link>
              <button onClick={handleLogout} className="block py-2 text-sm text-zinc-600 w-full text-left">Sign out</button>
            </>
          ) : (
            <>
              <Link to="/login" onClick={() => setMobileOpen(false)} className="block py-2 text-sm text-zinc-600">Sign in</Link>
              <Link to="/register" onClick={() => setMobileOpen(false)} className="block py-2 text-sm font-medium text-zinc-900">Get started</Link>
            </>
          )}
        </div>
      )}
    </header>
  )
}
