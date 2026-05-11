import { NavLink, Outlet, Link } from 'react-router-dom'
import { Package, ArrowLeft, Store } from 'lucide-react'

const links = [
  { to: '/seller/products', icon: Package, label: 'Products' },
]

export default function SellerLayout() {
  return (
    <div className="flex min-h-screen bg-zinc-50">
      {/* Sidebar */}
      <aside className="w-56 flex-shrink-0 bg-white border-r border-zinc-200 flex flex-col">
        <div className="px-5 py-5 border-b border-zinc-200">
          <div className="flex items-center gap-2 mb-0.5">
            <Store className="w-4 h-4 text-zinc-900" />
            <span className="font-semibold text-zinc-900 text-sm">Seller Hub</span>
          </div>
          <p className="text-xs text-zinc-400">storé</p>
        </div>

        <nav className="flex-1 px-3 py-4 space-y-1">
          {links.map(({ to, icon: Icon, label }) => (
            <NavLink key={to} to={to}
              className={({ isActive }) =>
                `flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm transition-colors ${
                  isActive
                    ? 'bg-zinc-900 text-white'
                    : 'text-zinc-600 hover:bg-zinc-50 hover:text-zinc-900'
                }`
              }
            >
              <Icon className="w-4 h-4" />
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="px-3 py-4 border-t border-zinc-200">
          <Link to="/products"
            className="flex items-center gap-2 px-3 py-2 rounded-lg text-sm text-zinc-500 hover:text-zinc-900 hover:bg-zinc-50 transition-colors">
            <ArrowLeft className="w-4 h-4" />
            Back to store
          </Link>
        </div>
      </aside>

      {/* Content */}
      <main className="flex-1 overflow-auto">
        <Outlet />
      </main>
    </div>
  )
}
