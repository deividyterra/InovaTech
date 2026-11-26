import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage } from '../../features/auth/LoginPage';
import { UsuariosPage } from '../../features/usuarios/UsuariosPage';

export function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/usuarios" element={<UsuariosPage />} />
        
        <Route path="*" element={<h2>404 - Página não encontrada</h2>} />
      </Routes>
    </BrowserRouter>
  );
}