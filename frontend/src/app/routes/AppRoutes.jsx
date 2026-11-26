import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from '../../features/auth/LoginPage';
import UsuariosPage from '../../features/usuario/UsuariosPage';

export function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Rota inicial, redireciona para /login por enquanto */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/usuarios" element={<UsuariosPage />} />

        {/* telas provisórias até criarmos os componentes reais dentro de features/ */}
        <Route path="/dashboard" element={<h1>Dashboard Principal</h1>} />
        
        {/* rota coringa para URLs inexistentes, página em construção ainda */}
        <Route path="*" element={<h1>404 - Página não encontrada</h1>} />
      </Routes>
    </BrowserRouter>
  );
}