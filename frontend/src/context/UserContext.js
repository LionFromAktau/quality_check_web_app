import { createContext, useState, useEffect, useContext } from 'react';
import keycloak from '../Components/services/keycloak';

export const UserContext = createContext();

export function UserProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isUserReady, setIsUserReady] = useState(false);

  useEffect(() => {
    const token = keycloak.token;

    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const now = Math.floor(Date.now() / 1000);

        if (payload?.exp && payload.exp < now) {
          keycloak.logout();
          setUser(null);
        } else {
          const roles = payload.realm_access?.roles || [];
          const appRole = roles.find(role => role.startsWith('role_')) || null;

          setUser({
            username: payload.preferred_username || payload.email || payload.sub,
            role: appRole,
            userId: payload.userId || payload.sub
          });
        }
      } catch (e) {
        console.error('Error decoding token:', e);
        keycloak.logout();
        setUser(null);
      }
    }

    setIsUserReady(true);
  }, []);

  const logout = () => {
    setUser(null);
    keycloak.logout();
  };

  return (
    <UserContext.Provider value={{ user, setUser, logout, isUserReady }}>
      {children}
    </UserContext.Provider>
  );
}

export function useUser() {
  return useContext(UserContext);
}
