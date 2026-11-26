import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage } from '../../features/auth/LoginPage';

export function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<LoginPage />} />
        
        {/* Próximo passo: substituir pelo componente real do CRUD de usuários */}
        <Route path="/usuarios" element={<h2>Tela de Gestão de Usuários (Em construção)</h2>} />
        
        <Route path="*" element={<h2>404 - Página não encontrada</h2>} />
      </Routes>
    </BrowserRouter>
  );
}