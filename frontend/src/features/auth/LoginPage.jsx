import { useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUsuario } from "../../services/usuario.api";
import "./LoginPage.css";
import risotoImg from "../../assets/risoto-cogumelos.png";

function IconUser() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
      <path d="M12 12c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm0 2c-3.33 0-10 1.67-10 5v3h20v-3c0-3.33-6.67-5-10-5z" />
    </svg>
  );
}

function IconEye({ aberto }) {
  if (aberto) {
    return (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
        <line x1="1" y1="1" x2="23" y2="23" />
      </svg>
    );
  }
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  );
}

// Extrai o payload do JWT (ex: perfil, sub, email) sem depender de bibliotecas externas
function extrairDadosToken(token) {
  try {
    const base64Url = token.split(".")[1];
    if (!base64Url) return {};
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch {
    return {};
  }
}

export default function LoginPage() {
  const navigate = useNavigate();

  // Etapas: "login" | "esqueci-senha" | "codigo" | "nova-senha"
  const [etapa, setEtapa] = useState("login");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);
  const [modalSucesso, setModalSucesso] = useState(false);

  // Campos de login / recuperação
  const [nomeUsuario, setNomeUsuario] = useState("");
  const [emailRecuperacao, setEmailRecuperacao] = useState("");
  const [senha, setSenha] = useState("");
  const [mostrarSenha, setMostrarSenha] = useState(false);

  // Código de 6 dígitos
  const [codigo, setCodigo] = useState(["", "", "", "", "", ""]);
  const inputsCodigoRef = useRef([]);

  // Nova senha
  const [novaSenha, setNovaSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [mostrarNovaSenha, setMostrarNovaSenha] = useState(false);
  const [mostrarConfirmarSenha, setMostrarConfirmarSenha] = useState(false);

  const irParaEtapa = (novaEtapa) => {
    setEtapa(novaEtapa);
    setErro("");
  };

  /* ========================
     HANDLERS DO CÓDIGO (6 DÍGITOS)
  ======================== */
  const handleCodigoChange = (index, valor) => {
    const digito = valor.replace(/\D/g, "").slice(-1);
    const novoCodigo = [...codigo];
    novoCodigo[index] = digito;
    setCodigo(novoCodigo);
    if (erro) setErro("");

    if (digito && index < 5) {
      inputsCodigoRef.current[index + 1]?.focus();
    }
  };

  const handleCodigoKeyDown = (index, e) => {
    if (e.key === "Backspace" && !codigo[index] && index > 0) {
      inputsCodigoRef.current[index - 1]?.focus();
    }
  };

  const handleCodigoPaste = (e) => {
    e.preventDefault();
    const colado = e.clipboardData.getData("text").replace(/\D/g, "").slice(0, 6);
    if (!colado) return;
    const novoCodigo = ["", "", "", "", "", ""];
    colado.split("").forEach((char, idx) => {
      novoCodigo[idx] = char;
    });
    setCodigo(novoCodigo);
    const proximoFoco = Math.min(colado.length, 5);
    inputsCodigoRef.current[proximoFoco]?.focus();
  };

  /* ========================
     SUBMISSÕES
  ======================== */
  const handleSubmitLogin = async (e) => {
    e.preventDefault();
    setErro("");
    setCarregando(true);

    try {
      const data = await loginUsuario({
        nomeUsuario: nomeUsuario.trim(),
        senha,
      });

      const payload = data?.token ? extrairDadosToken(data.token) : {};
      const perfil = String(payload.perfil || "").toUpperCase();

      if (perfil === "ADMINISTRADOR" || perfil === "ADMIN") {
        navigate("/usuarios");
      } else {
        navigate("/dashboard");
      }
    } catch (err) {
      setErro(
        err.message && !err.message.startsWith("Erro 40")
          ? err.message
          : "Usuário ou senha inválidos. Verifique os dados e tente novamente."
      );
    } finally {
      setCarregando(false);
    }
  };

  const handleSubmitEsqueciSenha = (e) => {
    e.preventDefault();
    // TODO: conectar quando o endpoint de envio de código OTP for criado no back-end
    setErro("");
    setCodigo(["", "", "", "", "", ""]);
    setEtapa("codigo");
  };

  const handleSubmitCodigo = (e) => {
    e.preventDefault();
    const valorCompleto = codigo.join("");
    if (valorCompleto.length < 6) {
      setErro("Informe os 6 dígitos do código enviado para o seu e-mail.");
      return;
    }
    // TODO: conectar validação do OTP no back-end
    setErro("");
    setEtapa("nova-senha");
  };

  const handleSubmitNovaSenha = (e) => {
    e.preventDefault();
    const regexSenhaForte = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,20}$/;
    if (!regexSenhaForte.test(novaSenha)) {
      setErro("A senha deve ter 8 a 20 caracteres, com maiúscula, minúscula, número e símbolo.");
      return;
    }
    if (novaSenha !== confirmarSenha) {
      setErro("As senhas não coincidem. Verifique os dois campos.");
      return;
    }
    // TODO: conectar redefinição de senha via token/código no back-end
    setErro("");
    setModalSucesso(true);
  };

  const handleVoltarInicio = () => {
    setModalSucesso(false);
    setErro("");
    setSenha("");
    setNovaSenha("");
    setConfirmarSenha("");
    setEtapa("login");
  };

  const isRecuperacao = etapa === "esqueci-senha" || etapa === "codigo";

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

      {/* LADO DIREITO - ETAPAS DINÂMICAS */}
      <main className="login-right">
        <div className="login-form-wrapper">
          {/* ================= ETAPA 1: LOGIN ================= */}
          {etapa === "login" && (
            <>
              <span className="login-eyebrow">BEM-VINDO DE VOLTA</span>
              <h4 className="login-title">
                {erro ? "Acesso não encontrado." : "Tudo pronto para o serviço?"}
              </h4>
              <p className="login-subtitle">
                Entre com sua conta para acessar sua área.
              </p>
              {erro && <p className="login-error-banner">{erro}</p>}

              <form onSubmit={handleSubmitLogin}>
                <div className="login-field">
                  <label htmlFor="login-usuario" className="login-label">
                    Nome de usuário
                  </label>
                  <div className="login-input-group">
                    <input
                      id="login-usuario"
                      type="text"
                      className={`login-input ${erro ? "login-input--error" : ""}`}
                      placeholder="seu.usuario"
                      value={nomeUsuario}
                      onChange={(e) => {
                        setNomeUsuario(e.target.value);
                        if (erro) setErro("");
                      }}
                      disabled={carregando}
                      required
                    />
                    <span className="login-input-icon" aria-hidden="true">
                      <IconUser />
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
                      className={`login-input ${erro ? "login-input--error" : ""}`}
                      placeholder="••••••••••••"
                      value={senha}
                      onChange={(e) => {
                        setSenha(e.target.value);
                        if (erro) setErro("");
                      }}
                      disabled={carregando}
                      required
                    />
                    <button
                      type="button"
                      className="login-eye-btn"
                      onClick={() => setMostrarSenha((prev) => !prev)}
                      aria-label={mostrarSenha ? "Ocultar senha" : "Mostrar senha"}
                    >
                      <IconEye aberto={mostrarSenha} />
                    </button>
                  </div>

                  <div className="login-forgot-row">
                    <button
                      type="button"
                      className="login-forgot-link"
                      onClick={() => irParaEtapa("esqueci-senha")}
                    >
                      Esqueceu senha?
                    </button>
                  </div>
                </div>

                <button
                  type="submit"
                  className="login-submit-btn"
                  disabled={carregando}
                >
                  <span>{carregando ? "Entrando..." : "Entrar na minha área"}</span>
                  {!carregando && <span aria-hidden="true">→</span>}
                </button>

                <p className="login-redirect-hint">
                  Você será direcionado automaticamente conforme seu perfil.
                </p>
              </form>
            </>
          )}

          {/* ================= ETAPA 2: ESQUECI SENHA (E-MAIL) ================= */}
          {etapa === "esqueci-senha" && (
            <>
              <span className="login-eyebrow">ESQUECI MINHA SENHA, E AGORA?</span>
              <h4 className="login-title">Digite seu e-mail cadastrado.</h4>
              <p className="login-subtitle">
                Digite seu e-mail registrado para enviarmos um código de
                verificação para alteração da senha.
              </p>

              <form onSubmit={handleSubmitEsqueciSenha}>
                <div className="login-field">
                  <label htmlFor="recuperar-email" className="login-label">
                    E-mail
                  </label>
                  <div className="login-input-group">
                    <input
                      id="recuperar-email"
                      type="email"
                      className="login-input"
                      placeholder="seu.nome@restaurante.com"
                      value={emailRecuperacao}
                      onChange={(e) => setEmailRecuperacao(e.target.value)}
                      required
                    />
                    <span className="login-input-icon" aria-hidden="true">
                      <IconUser />
                    </span>
                  </div>

                  <div className="login-forgot-row">
                    <button
                      type="button"
                      className="login-forgot-link"
                      onClick={() => irParaEtapa("login")}
                    >
                      ← Voltar para o login
                    </button>
                  </div>
                </div>

                <button type="submit" className="login-submit-btn">
                  <span>Receber Código</span>
                  <span aria-hidden="true">→</span>
                </button>

                <p className="login-redirect-hint">
                  O código chegará no e-mail informado em instantes.
                </p>
              </form>
            </>
          )}

          {/* ================= ETAPA 3: CÓDIGO DE 6 DÍGITOS ================= */}
          {etapa === "codigo" && (
            <>
              <span className="login-eyebrow">ESQUECI MINHA SENHA, E AGORA?</span>
              <h4 className="login-title">
                {erro ? "Código incorreto." : "Digite o código recebido."}
              </h4>
              <p className="login-subtitle">
                {erro
                  ? "Verifique os 6 dígitos enviados para o seu e-mail e tente novamente."
                  : "Digite o código recebido por e-mail."}
              </p>

              <form onSubmit={handleSubmitCodigo}>
                <div className="login-field">
                  <div className="login-otp-group" onPaste={handleCodigoPaste}>
                    {codigo.map((digito, index) => (
                      <input
                        key={index}
                        ref={(el) => (inputsCodigoRef.current[index] = el)}
                        type="text"
                        inputMode="numeric"
                        maxLength={1}
                        aria-label={`Dígito ${index + 1}`}
                        className={`login-otp-box ${erro ? "login-otp-box--error" : ""}`}
                        value={digito}
                        onChange={(e) => handleCodigoChange(index, e.target.value)}
                        onKeyDown={(e) => handleCodigoKeyDown(index, e)}
                      />
                    ))}
                  </div>

                  <div className="login-forgot-row">
                    <button
                      type="button"
                      className="login-forgot-link"
                      onClick={() => irParaEtapa("esqueci-senha")}
                    >
                      Alterar e-mail
                    </button>
                    <button
                      type="button"
                      className="login-forgot-link"
                      onClick={() => {
                        setCodigo(["", "", "", "", "", ""]);
                        setErro("");
                      }}
                    >
                      Reenviar código
                    </button>
                  </div>
                </div>

                <button type="submit" className="login-submit-btn">
                  <span>Enviar Código</span>
                  <span aria-hidden="true">→</span>
                </button>

                <p className="login-redirect-hint">
                  Assim que confirmar o código, você poderá digitar sua nova senha.
                </p>
              </form>
            </>
          )}

          {/* ================= ETAPA 4: NOVA SENHA ================= */}
          {etapa === "nova-senha" && (
            <>
              <span className="login-eyebrow">ESQUECI MINHA SENHA, E AGORA?</span>
              <h4 className="login-title">Realize a alteração da senha</h4>
              <p className="login-subtitle">
                Digite sua nova senha nos dois campos abaixo.
              </p>
              {erro && <p className="login-error-banner">{erro}</p>}

              <form onSubmit={handleSubmitNovaSenha}>
                <div className="login-field">
                  <label htmlFor="nova-senha" className="login-label">
                    Nova senha
                  </label>
                  <div className="login-input-group">
                    <input
                      id="nova-senha"
                      type={mostrarNovaSenha ? "text" : "password"}
                      className={`login-input ${erro ? "login-input--error" : ""}`}
                      placeholder="••••••••••••"
                      value={novaSenha}
                      onChange={(e) => {
                        setNovaSenha(e.target.value);
                        if (erro) setErro("");
                      }}
                      required
                    />
                    <button
                      type="button"
                      className="login-eye-btn"
                      onClick={() => setMostrarNovaSenha((prev) => !prev)}
                      aria-label={mostrarNovaSenha ? "Ocultar senha" : "Mostrar senha"}
                    >
                      <IconEye aberto={mostrarNovaSenha} />
                    </button>
                  </div>
                </div>

                <div className="login-field">
                  <label htmlFor="confirmar-senha" className="login-label">
                    Confirme sua nova senha
                  </label>
                  <div className="login-input-group">
                    <input
                      id="confirmar-senha"
                      type={mostrarConfirmarSenha ? "text" : "password"}
                      className={`login-input ${erro ? "login-input--error" : ""}`}
                      placeholder="••••••••••••"
                      value={confirmarSenha}
                      onChange={(e) => {
                        setConfirmarSenha(e.target.value);
                        if (erro) setErro("");
                      }}
                      required
                    />
                    <button
                      type="button"
                      className="login-eye-btn"
                      onClick={() => setMostrarConfirmarSenha((prev) => !prev)}
                      aria-label={mostrarConfirmarSenha ? "Ocultar senha" : "Mostrar senha"}
                    >
                      <IconEye aberto={mostrarConfirmarSenha} />
                    </button>
                  </div>
                </div>

                <button type="submit" className="login-submit-btn">
                  <span>Confirmar alteração de senha</span>
                  <span aria-hidden="true">→</span>
                </button>

                <p className="login-redirect-hint">
                  As senhas presentes nos dois campos devem ser idênticas.
                </p>
              </form>
            </>
          )}

          {/* RODAPÉ FIXO DO FORMULÁRIO */}
          <hr className="login-divider" />

          <p className="login-help-text">
            {etapa === "nova-senha"
              ? "Se qualquer erro persistir,"
              : isRecuperacao
              ? "Não possui acesso ao e-mail?"
              : "Precisa de outro acesso?"}
            <br />
            Converse com um administrador.
          </p>

          <p className="login-restricted-footer">
            Acesso exclusivo à equipe autorizada.
          </p>
        </div>
      </main>

      {/* ================= MODAL DE SUCESSO ================= */}
      {modalSucesso && (
        <div className="login-modal-backdrop" role="dialog" aria-modal="true">
          <div className="login-modal-card">
            <div className="login-modal-body">
              <span className="login-modal-eyebrow">PROBLEMA RESOLVIDO!</span>
              <h4 className="login-modal-title">Senha alterada com sucesso!</h4>
              <p className="login-modal-text">
                Clique abaixo para retornar a tela inicial e realizar o login.
              </p>
            </div>
            <button
              type="button"
              className="login-modal-action"
              onClick={handleVoltarInicio}
            >
              <span>Voltar para tela de login</span>
              <span aria-hidden="true">→</span>
            </button>
          </div>
        </div>
      )}
    </div>
  );
}