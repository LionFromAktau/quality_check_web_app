// Components/services/RoleProtectedRoute.jsx
import { Navigate } from 'react-router-dom';
import { useUser } from '../../context/UserContext';

export default function RoleProtectedRoute({ children, allowedRoles }) {
  const { user, isUserReady } = useUser();

  if (!isUserReady) {
    return <div>Loading...</div>; 
  }

  if (!user) {
    return <Navigate to="/login" replace />; 
  }

  const userRoles = user.roles || [];

  const hasAccess =
    !allowedRoles || allowedRoles.length === 0 || userRoles.some(role => allowedRoles.includes(role));

  if (!hasAccess) {
    return <Navigate to="/" replace />;
  }

  return children;
}
