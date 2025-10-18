import React from 'react';
import { Navigate } from 'react-router-dom';
import { tokenUtils } from '../utils/token';

interface RequireAuthProps {
  children: React.ReactNode;
}

const RequireAuth: React.FC<RequireAuthProps> = ({ children }) => {
  const isAuthenticated = tokenUtils.isAuthenticated();
  
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  
  return <>{children}</>;
};

export default RequireAuth;
