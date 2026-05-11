import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { ProtectedRoute, AdminRoute, SellerRoute } from './components/ProtectedRoute'
import Navbar from './components/Navbar'

import Login from './pages/Login'
import Register from './pages/Register'
import Products from './pages/Products'
import ProductDetail from './pages/ProductDetail'
import Cart from './pages/Cart'
import Checkout from './pages/Checkout'
import Orders from './pages/Orders'
import OrderDetail from './pages/OrderDetail'
import Profile from './pages/Profile'

import AdminLayout from './pages/admin/AdminLayout'
import AdminProducts from './pages/admin/AdminProducts'
import AdminOrders from './pages/admin/AdminOrders'
import AdminUsers from './pages/admin/AdminUsers'

import SellerLayout from './pages/seller/SellerLayout'
import SellerProducts from './pages/seller/SellerProducts'

function MainLayout({ children }) {
  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      {children}
    </div>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public */}
          <Route path="/login"    element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Main layout */}
          <Route path="/" element={<MainLayout><Navigate to="/products" replace /></MainLayout>} />
          <Route path="/products"    element={<MainLayout><Products /></MainLayout>} />
          <Route path="/products/:id" element={<MainLayout><ProductDetail /></MainLayout>} />
          <Route path="/cart"        element={<MainLayout><Cart /></MainLayout>} />
          {/* Checkout is accessible without auth — it handles auth internally */}
          <Route path="/checkout" element={<MainLayout><Checkout /></MainLayout>} />

          {/* Protected */}
          <Route path="/orders"   element={<ProtectedRoute><MainLayout><Orders /></MainLayout></ProtectedRoute>} />
          <Route path="/orders/:id" element={<ProtectedRoute><MainLayout><OrderDetail /></MainLayout></ProtectedRoute>} />
          <Route path="/profile"  element={<ProtectedRoute><MainLayout><Profile /></MainLayout></ProtectedRoute>} />

          {/* Admin */}
          <Route path="/admin" element={<AdminRoute><AdminLayout /></AdminRoute>}>
            <Route index element={<Navigate to="/admin/products" replace />} />
            <Route path="products" element={<AdminProducts />} />
            <Route path="orders"   element={<AdminOrders />} />
            <Route path="users"    element={<AdminUsers />} />
          </Route>

          {/* Seller */}
          <Route path="/seller" element={<SellerRoute><SellerLayout /></SellerRoute>}>
            <Route index element={<Navigate to="/seller/products" replace />} />
            <Route path="products" element={<SellerProducts />} />
          </Route>

          <Route path="*" element={<Navigate to="/products" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
