import { useState } from "react";
import "./LoginPage.css";
import risotoImg from "../../assets/risoto-cogumelos.png";

export default function LoginPage({ onEsqueceuSenha }) {
  const [identificador, setIdentificador] = useState("");
  const [senha, setSenha] = useState("");
  const [mostrarSenha, setMostrarSenha] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    // Etapa atual: apenas visual (a integração com POST /api/usuario/login será feita depois)
    console.log("Dados preenchidos no formulário:", { identificador, senha });
  };

  return (
    <div className="login-container">
      {/* LADO ESQUERDO - INSTITUCIONAL */}
      <aside className="login-left">
        <div>
          <h1 className="login-brand">padre bueno</h1>

          <div className="login-hero">
            <h2 className="login-hero-title">
              Uma boa operação.
              <br />
              Uma ótima
              <br />
              experiência.
            </h2>
            <p className="login-hero-subtitle">
              Da primeira comanda ao último serviço, sua equipe no mesmo ritmo.
            </p>
          </div>

          <div className="login-dish-card">
            <img
              src={risotoImg}
              alt="Risoto de cogumelos"
              className="login-dish-image"
            />
            <div className="login-dish-badge">Risoto de cogumelos</div>
          </div>
        </div>

        <p className="login-left-footer">
          RESTAURANTE E CANTINA PADRE BUENO / PAINEL DA EQUIPE
        </p>
      </aside>

      {/* LADO DIREITO - FORMULÁRIO */}
      <main className="login-right">
        <div className="login-form-wrapper">
          <span className="login-eyebrow">BEM-VINDO DE VOLTA</span>
          <h4 className="login-title">Tudo pronto para o serviço?</h4>
          <p className="login-subtitle">
            Entre com sua conta para acessar sua área.
          </p>

          <form onSubmit={handleSubmit}>
            <div className="login-field">
              <label htmlFor="login-email" className="login-label">
                E-mail
              </label>
              <div className="login-input-group">
                <input
                  id="login-email"
                  type="text"
                  className="login-input"
                  placeholder="seu.nome@restaurante.com"
                  value={identificador}
                  onChange={(e) => setIdentificador(e.target.value)}
                  required
                />
                <span className="login-input-icon" aria-hidden="true">
                  <svg
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="currentColor"
                  >
                    <path d="M12 12c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm0 2c-3.33 0-10 1.67-10 5v3h20v-3c0-3.33-6.67-5-10-5z" />
                  </svg>
                </span>
              </div>
            </div>

            <div className="login-field">
              <label htmlFor="login-senha" className="login-label">
                Senha
              </label>
              <div className="login-input-group">
                <input
                  id="login-senha"
                  type={mostrarSenha ? "text" : "password"}
                  className="login-input"
                  placeholder="••••••••••••"
                  value={senha}
                  onChange={(e) => setSenha(e.target.value)}
                  required
                />
                <button
                  type="button"
                  className="login-eye-btn"
                  onClick={() => setMostrarSenha((prev) => !prev)}
                  aria-label={mostrarSenha ? "Ocultar senha" : "Mostrar senha"}
                >
                  {mostrarSenha ? (
                    <svg
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                    >
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                      <line x1="1" y1="1" x2="23" y2="23" />
                    </svg>
                  ) : (
                    <svg
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                    >
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                      <circle cx="12" cy="12" r="3" />
                    </svg>
                  )}
                </button>
              </div>

              <div className="login-forgot-row">
                <button
                  type="button"
                  className="login-forgot-link"
                  onClick={onEsqueceuSenha}
                >
                  Esqueceu senha?
                </button>
              </div>
            </div>

            <button type="submit" className="login-submit-btn">
              <span>Entrar na minha área</span>
              <span aria-hidden="true">→</span>
            </button>

            <p className="login-redirect-hint">
              Você será direcionado automaticamente conforme seu perfil.
            </p>
          </form>

          <hr className="login-divider" />

          <p className="login-help-text">
            Precisa de outro acesso?
            <br />
            Converse com um administrador.
          </p>

          <p className="login-restricted-footer">
            Acesso exclusivo à equipe autorizada.
          </p>
        </div>
      </main>
    </div>
  );
}