import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

export function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Rota inicial, redireciona para /login por enquanto */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        
        {/* telas provisórias até criarmos os componentes reais dentro de features/ */}
        <Route path="/login" element={<h1>Tela de Login - InovaTech</h1>} />
        <Route path="/dashboard" element={<h1>Dashboard Principal</h1>} />
        
        {/* rota coringa para URLs inexistentes, página em construção ainda */}
        <Route path="*" element={<h1>404 - Página não encontrada</h1>} />
      </Routes>
    </BrowserRouter>
  );
}