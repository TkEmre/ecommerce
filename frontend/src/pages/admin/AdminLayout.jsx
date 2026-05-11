import { NavLink, Outlet, Link } from 'react-router-dom'
import { LayoutGrid, ShoppingCart, Users, ArrowLeft } from 'lucide-react'
import { Separator } from '../../components/ui/separator'

const links = [
  { to: '/admin/products', label: 'Products', icon: LayoutGrid },
  { to: '/admin/orders',   label: 'Orders',   icon: ShoppingCart },
  { to: '/admin/users',    label: 'Users',    icon: Users },
]

export default function AdminLayout() {
  return (
    <div className="flex min-h-screen bg-white">
      {/* Sidebar */}
      <aside className="w-56 border-r border-zinc-200 flex flex-col shrink-0">
        <div className="px-4 py-5 border-b border-zinc-200">
          <Link to="/" className="flex items-center gap-2 text-xs text-zinc-500 hover:text-zinc-900 transition-colors mb-4">
            <ArrowLeft className="w-3.5 h-3.5" />
            Back to store
          </Link>
          <p className="text-sm font-semibold text-zinc-900">storé admin</p>
        </div>

        <nav className="flex-1 p-3 space-y-0.5">
          {links.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm transition-colors ${
                  isActive
                    ? 'bg-zinc-900 text-white font-medium'
                    : 'text-zinc-500 hover:bg-zinc-100 hover:text-zinc-900'
                }`
              }
            >
              <Icon className="w-4 h-4" />
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="p-4 border-t border-zinc-200">
          <p className="text-xs text-zinc-400">Admin panel</p>
        </div>
      </aside>

      {/* Main */}
      <main className="flex-1 bg-zinc-50 overflow-auto">
        <Outlet />
      </main>
    </div>
  )
}
