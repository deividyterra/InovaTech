import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Lock, Mail, Eye, EyeOff } from 'lucide-react';
import './LoginPage.css';

export function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [mostrarSenha, setMostrarSenha] = useState(false);

  function handleLogin(event) {
    event.preventDefault();

    // redireciona direto para a gestão de usuários para a entrega parcial da
    navigate('/usuarios');
  }

  return (

    <div className="LoginPage"> 
    <div className="SideInfos-container">
        <div className="logo-header">
            <h1>padre bueno</h1>
        </div>
        <div className="side-info">
            <h2>Uma boa operação. Uma ótima experiência.</h2>
        </div>
    </div>

    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h3>Tudo pronto para o serviço?</h3>
          <p>Entre com sua conta para acessar sua área.</p>
        </div>

        <form onSubmit={handleLogin} className="login-form">
          <div className="form-group">
            <label htmlFor="email">E-mail</label>
            <div className="input-wrapper">
              <Mail size={18} className="input-icon" />
              <input
                id="email"
                type="email"
                placeholder="admin@padrebueno.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="senha">Senha</label>
            <div className="input-wrapper">
              <Lock size={18} className="input-icon" />
              <input
                id="senha"
                type={mostrarSenha ? 'text' : 'password'}
                placeholder="••••••••"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                required
              />
              <button
                type="button"
                className="toggle-password"
                onClick={() => setMostrarSenha(!mostrarSenha)}
                aria-label="Mostrar ou ocultar senha"
              >
                {mostrarSenha ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          <button type="submit" className="btn-login">
            Entrar
          </button>
        </form>
      </div>
    </div>
    </div>
  );
}