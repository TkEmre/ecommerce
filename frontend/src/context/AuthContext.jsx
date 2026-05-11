import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

function parseJwt(token) {
  try {
    return JSON.parse(atob(token.split('.')[1]))
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'))
  const [user, setUser] = useState(null)

  useEffect(() => {
    if (token) {
      const payload = parseJwt(token)
      if (payload) {
        // JWT'de roles comma-separated string: "ROLE_ADMIN,ROLE_USER"
        const rolesStr = payload.roles || ''
        setUser({
          username: payload.sub,
          isAdmin: rolesStr.includes('ADMIN'),
          isSeller: rolesStr.includes('SELLER'),
        })
      }
    } else {
      setUser(null)
    }
  }, [token])

  function login(jwt) {
    localStorage.setItem('token', jwt)
    setToken(jwt)
  }

  function logout() {
    localStorage.removeItem('token')
    setToken(null)
  }

  return (
    <AuthContext.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
