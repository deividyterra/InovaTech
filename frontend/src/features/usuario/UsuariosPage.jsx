import { useNavigate } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import {
  alterarSenhaUsuario,
  atualizarUsuario,
  cadastrarUsuario,
  excluirUsuario,
  listarUsuarios,
} from "../../services/usuario.api";
import "./UsuariosPage.css";

/* ========================
   CONSTANTES
======================== */

const PERFIS = [
  { value: "GARCOM", label: "Garçom", cor: "yellow" },
  { value: "CHEF_COZINHA", label: "Cozinheiro", cor: "green" },
  { value: "GESTOR_PEDIDOS", label: "Gestor de pedidos", cor: "blue" },
  { value: "CAIXA", label: "Caixa", cor: "purple" },
  { value: "ADMINISTRADOR", label: "Administrador", cor: "red" },
];

const POR_PAGINA = 5;

const FORM_INICIAL = {
  nome: "",
  nomeUsuario: "",
  cpf: "",
  email: "",
  senha: "",
  perfil: "GARCOM",
};

const MENU = [
  { label: "Pedidos", icon: "pedidos" },
  { label: "Cardápio", icon: "cardapio" },
  { label: "Visão geral", icon: "visao" },
  { label: "Estoque", icon: "estoque" },
  { label: "Usuários", icon: "usuarios", ativo: true },
];

/* ========================
   AUXILIARES
======================== */

function Icon({ name }) {
  const paths = {
    pedidos: <path d="M4 5h4v14H4zM10 5h4v9h-4zM16 5h4v6h-4z" />,
    cardapio: <path d="M7 3v8M5 3v5a2 2 0 0 0 4 0V3M7 11v10M17 3c-2 2-3 5-3 8h3v10" />,
    visao: <path d="M4 4h7v7H4zM13 4h7v7h-7zM4 13h7v7H4zM13 13h7v7h-7z" />,
    estoque: <path d="M12 3l8 4v10l-8 4-8-4V7zM4 7l8 4 8-4M12 11v10" />,
    usuarios: (
      <>
        <circle cx="9" cy="8" r="3.5" />
        <path d="M2.5 20c0-3.5 3-6 6.5-6s6.5 2.5 6.5 6M16 4.5a3.5 3.5 0 0 1 0 7M18 14c2 .8 3.5 2.8 3.5 6" />
      </>
    ),
  };

  return (
    <svg
      className="sidebar__icon"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.6"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      {paths[name]}
    </svg>
  );
}

function IconeLapis() {
  return (
    <svg
      className="modal-usuario__lapis"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <path d="M12 20h9" />
      <path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z" />
    </svg>
  );
}

function mascararCpf(valor) {
  const n = valor.replace(/\D/g, "").slice(0, 11);
  return n
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}

const perfilInfo = (valor) => {
  const normalizado = String(valor || "").toUpperCase();
  if (normalizado === "ADMIN") {
    return PERFIS.find((p) => p.value === "ADMINISTRADOR");
  }
  return (
    PERFIS.find((p) => p.value === normalizado) || {
      label: valor || "Usuário",
      cor: "yellow",
    }
  );
};

const REGEX_SENHA_FORTE = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,20}$/;

/* ========================
   PÁGINA
======================== */

export default function UsuariosPage() {
  const [usuarios, setUsuarios] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erroLista, setErroLista] = useState("");
  const [salvando, setSalvando] = useState(false);

  const [abaAtiva, setAbaAtiva] = useState("todos");
  const [busca, setBusca] = useState("");
  const [filtroPerfil, setFiltroPerfil] = useState("todos");
  const [pagina, setPagina] = useState(1);

  // Formulário lateral (Novo usuário)
  const [form, setForm] = useState(FORM_INICIAL);

  // Modal de feedback de cadastro: null | "sucesso" | "erro"
  const [modalStatus, setModalStatus] = useState(null);
  const [mensagemErroModal, setMensagemErroModal] = useState("");

  // Modal de ação do usuário: null | { modo: "visualizar" | "editar" | "excluir", usuario }
  const [modalUsuario, setModalUsuario] = useState(null);
  const [formEdicao, setFormEdicao] = useState({
    nome: "",
    cpf: "",
    email: "",
    senha: "",
  });

  async function carregarUsuarios() {
    setCarregando(true);
    setErroLista("");
    try {
      const lista = await listarUsuarios();
      setUsuarios(lista);
    } catch (err) {
      setErroLista(err.message || "Não foi possível carregar os usuários.");
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarUsuarios();
  }, []);

const ehAdmin = (u) => {
  const p = String(u.perfil || "").toUpperCase();
  return p === "ADMINISTRADOR" || p === "ADMIN";
};

const filtrados = useMemo(() => {
  const termo = busca.trim().toLowerCase();
  return usuarios.filter((u) => {
    const bateAba = abaAtiva === "todos" || ehAdmin(u);
    const bateBusca =
      !termo ||
      u.nome?.toLowerCase().includes(termo) ||
      u.email?.toLowerCase().includes(termo) ||
      u.nomeUsuario?.toLowerCase().includes(termo);
    const batePerfil =
      filtroPerfil === "todos" ||
      String(u.perfil || "").toUpperCase() === filtroPerfil;
    return bateAba && bateBusca && batePerfil;
  });
}, [usuarios, abaAtiva, busca, filtroPerfil]);

  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / POR_PAGINA));
  const paginaAtual = Math.min(pagina, totalPaginas);
  const visiveis = filtrados.slice(
    (paginaAtual - 1) * POR_PAGINA,
    paginaAtual * POR_PAGINA
  );
  const totalAdmins = usuarios.filter(ehAdmin).length;

  function handleChange(e) {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: name === "cpf" ? mascararCpf(value) : value }));
  }

  function handleEdicaoChange(e) {
    const { name, value } = e.target;
    setFormEdicao((f) => ({
      ...f,
      [name]: name === "cpf" ? mascararCpf(value) : value,
    }));
  }

function iniciarNovoAdmin() {
  setForm({ ...FORM_INICIAL, perfil: "ADMINISTRADOR" });
}

  function abrirModalUsuario(modo, usuario) {
    setFormEdicao({
      nome: usuario.nome || "",
      cpf: usuario.cpf || "",
      email: usuario.email || "",
      senha: "••••••••••••",
    });
    setModalUsuario({ modo, usuario });
  }
const navigate = useNavigate();

function handleLogout(e) {
  e.preventDefault();
  localStorage.removeItem("token");
  navigate("/login");
}
  async function handleSubmitNovo(e) {
    e.preventDefault();
    if (salvando) return;

    try {
      const nomeLimpo = form.nome.trim();
      const loginLimpo = form.nomeUsuario.trim();
      const cpfLimpo = form.cpf.replace(/\D/g, "");
      const emailNormalizado = form.email.trim().toLowerCase();

      if (nomeLimpo.length < 8 || nomeLimpo.length > 60) {
        throw new Error("O nome completo deve ter entre 8 e 60 caracteres.");
      }
      if (loginLimpo.length < 8 || loginLimpo.length > 30) {
        throw new Error("O nome de usuário (login) deve ter entre 8 e 30 caracteres.");
      }
      if (cpfLimpo.length !== 11) {
        throw new Error("Informe um CPF válido com 11 dígitos.");
      }
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailNormalizado)) {
        throw new Error("Informe um e-mail válido.");
      }
      if (!REGEX_SENHA_FORTE.test(form.senha)) {
        throw new Error(
          "A senha deve ter de 8 a 20 caracteres, com letra maiúscula, minúscula, número e caractere especial."
        );
      }

      setSalvando(true);
      await cadastrarUsuario(form);

      setForm(FORM_INICIAL);
      setPagina(1);
      setModalStatus("sucesso");
      await carregarUsuarios();
    } catch (err) {
      setMensagemErroModal(
        err.message ||
          "Ocorreu um erro ao tentar registrar um novo usuário. Confira os dados e tente novamente."
      );
      setModalStatus("erro");
    } finally {
      setSalvando(false);
    }
  }

  async function handleSalvarEdicao(e) {
    e.preventDefault();
    if (!modalUsuario || salvando) return;

    const { usuario } = modalUsuario;
    const nomeLimpo = formEdicao.nome.trim();
    const emailLimpo = formEdicao.email.trim();
    const cpfLimpo = formEdicao.cpf;

    if (
      nomeLimpo.length < 8 ||
      nomeLimpo.length > 60 ||
      cpfLimpo.replace(/\D/g, "").length !== 11 ||
      !emailLimpo
    ) {
      alert("Verifique os campos: nome (8 a 60 caracteres), CPF válido e e-mail.");
      return;
    }

    const alterouSenha =
      formEdicao.senha && formEdicao.senha !== "••••••••••••";

    if (alterouSenha && !REGEX_SENHA_FORTE.test(formEdicao.senha)) {
      alert(
        "A nova senha deve ter de 8 a 20 caracteres, com letra maiúscula, minúscula, número e caractere especial."
      );
      return;
    }

    try {
      setSalvando(true);
      const atualizado = await atualizarUsuario(usuario.id, {
        nome: nomeLimpo,
        email: emailLimpo,
        cpf: cpfLimpo,
      });

      if (alterouSenha) {
        await alterarSenhaUsuario(usuario.id, {
          novaSenha: formEdicao.senha,
          confirmarSenha: formEdicao.senha,
        });
      }

      setUsuarios((lista) =>
        lista.map((u) => (u.id === usuario.id ? { ...u, ...atualizado } : u))
      );
      setModalUsuario(null);
    } catch (err) {
      alert(err.message || "Erro ao atualizar usuário.");
    } finally {
      setSalvando(false);
    }
  }

  async function handleConfirmarExclusao() {
    if (!modalUsuario || salvando) return;
    const { usuario } = modalUsuario;

    try {
      setSalvando(true);
      await excluirUsuario(usuario.id);
      setUsuarios((lista) => lista.filter((u) => u.id !== usuario.id));
      setModalUsuario(null);
    } catch (err) {
      alert(err.message || "Erro ao excluir usuário.");
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div className="usuarios-layout">
      {/* ===== MODAL DE FEEDBACK DO CADASTRO (SUCESSO / ERRO) ===== */}
      {modalStatus && (
        <div
          className="modal-overlay"
          role="dialog"
          aria-modal="true"
          onClick={() => setModalStatus(null)}
        >
          <div
            className={`modal-feedback modal-feedback--${modalStatus}`}
            onClick={(e) => e.stopPropagation()}
          >
            {modalStatus === "sucesso" ? (
              <>
                <div className="modal-feedback__corpo">
                  <span className="modal-feedback__tag">USUÁRIO CADASTRADO</span>
                  <h3 className="modal-feedback__titulo">
                    Novo usuário cadastrado com sucesso!
                  </h3>
                  <p className="modal-feedback__descricao">
                    Informe os dados de acesso para o novo usuário.
                  </p>
                </div>
                <button
                  type="button"
                  className="modal-feedback__rodape"
                  onClick={() => setModalStatus(null)}
                >
                  <span>Voltar para Usuários</span>
                  <span>→</span>
                </button>
              </>
            ) : (
              <>
                <div className="modal-feedback__corpo">
                  <span className="modal-feedback__tag">OPS...</span>
                  <h3 className="modal-feedback__titulo">
                    Erro ao cadastrar novo usuário.
                  </h3>
                  <p className="modal-feedback__descricao">{mensagemErroModal}</p>
                </div>
                <button
                  type="button"
                  className="modal-feedback__rodape"
                  onClick={() => setModalStatus(null)}
                >
                  <span>Voltar para Usuários</span>
                  <span>→</span>
                </button>
              </>
            )}
          </div>
        </div>
      )}

      {/* ===== MODAL UNIFICADO: VISUALIZAR / EDITAR / EXCLUIR ===== */}
      {modalUsuario && (
        <div
          className="modal-overlay modal-overlay--centro"
          role="dialog"
          aria-modal="true"
          onClick={() => setModalUsuario(null)}
        >
          <form
            className={`modal-usuario modal-usuario--${modalUsuario.modo}`}
            onClick={(e) => e.stopPropagation()}
            onSubmit={handleSalvarEdicao}
            noValidate
          >
            <button
              type="button"
              className="modal-usuario__fechar"
              aria-label="Fechar modal"
              onClick={() => setModalUsuario(null)}
            >
              ×
            </button>

            <h3 className="modal-usuario__titulo">
              {modalUsuario.modo === "visualizar" && "Dados do Usuário"}
              {modalUsuario.modo === "editar" && "Editar dados do usuário"}
              {modalUsuario.modo === "excluir" && "Deseja excluir este usuário?"}
            </h3>

            <div className="modal-usuario__campos">
              <label className="campo">
                <span>Nome completo</span>
                <div className="modal-usuario__input-wrap">
                  <input
                    type="text"
                    name="nome"
                    className="input"
                    value={formEdicao.nome}
                    onChange={handleEdicaoChange}
                    readOnly={modalUsuario.modo !== "editar"}
                  />
                  {modalUsuario.modo === "editar" && <IconeLapis />}
                </div>
              </label>

              <label className="campo">
                <span>CPF</span>
                <div className="modal-usuario__input-wrap">
                  <input
                    type="text"
                    name="cpf"
                    className="input"
                    value={formEdicao.cpf}
                    onChange={handleEdicaoChange}
                    readOnly={modalUsuario.modo !== "editar"}
                  />
                  {modalUsuario.modo === "editar" && <IconeLapis />}
                </div>
              </label>

              <label className="campo">
                <span>E-mail</span>
                <div className="modal-usuario__input-wrap">
                  <input
                    type="email"
                    name="email"
                    className="input"
                    value={formEdicao.email}
                    onChange={handleEdicaoChange}
                    readOnly={modalUsuario.modo !== "editar"}
                  />
                  {modalUsuario.modo === "editar" && <IconeLapis />}
                </div>
              </label>

              <label className="campo">
                <span>Senha</span>
                <div className="modal-usuario__input-wrap">
                  <input
                    type="password"
                    name="senha"
                    className="input"
                    value={formEdicao.senha}
                    onFocus={() => {
                      if (
                        modalUsuario.modo === "editar" &&
                        formEdicao.senha === "••••••••••••"
                      ) {
                        setFormEdicao((f) => ({ ...f, senha: "" }));
                      }
                    }}
                    onChange={handleEdicaoChange}
                    readOnly={modalUsuario.modo !== "editar"}
                  />
                  {modalUsuario.modo === "editar" && <IconeLapis />}
                </div>
              </label>

              <label className="campo">
                <span>Perfil de acesso · não poderá ser alterado</span>
                <div className="modal-usuario__input-wrap">
                  <input
                    type="text"
                    className="input"
                    value={perfilInfo(modalUsuario.usuario.perfil)?.label || ""}
                    readOnly
                  />
                </div>
              </label>
            </div>

            {modalUsuario.modo === "visualizar" && (
              <div className="modal-usuario__acoes">
                <button
                  type="button"
                  className="modal-usuario__btn modal-usuario__btn--verde"
                  onClick={() =>
                    setModalUsuario((m) => ({ ...m, modo: "editar" }))
                  }
                >
                  Editar usuário
                </button>
                <button
                  type="button"
                  className="modal-usuario__btn modal-usuario__btn--vermelho"
                  onClick={() =>
                    setModalUsuario((m) => ({ ...m, modo: "excluir" }))
                  }
                >
                  Excluir usuário
                </button>
              </div>
            )}

            {modalUsuario.modo === "editar" && (
              <div className="modal-usuario__acoes">
                <button
                  type="button"
                  className="modal-usuario__btn modal-usuario__btn--vermelho"
                  onClick={() => setModalUsuario(null)}
                  disabled={salvando}
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="modal-usuario__btn modal-usuario__btn--verde"
                  disabled={salvando}
                >
                  {salvando ? "Salvando..." : "Salvar"}
                </button>
              </div>
            )}

            {modalUsuario.modo === "excluir" && (
              <div className="modal-usuario__acoes">
                <button
                  type="button"
                  className="modal-usuario__btn modal-usuario__btn--verde"
                  onClick={() => setModalUsuario(null)}
                  disabled={salvando}
                >
                  Cancelar
                </button>
                <button
                  type="button"
                  className="modal-usuario__btn modal-usuario__btn--vermelho"
                  onClick={handleConfirmarExclusao}
                  disabled={salvando}
                >
                  {salvando ? "Excluindo..." : "Excluir"}
                </button>
              </div>
            )}
          </form>
        </div>
      )}

      {/* ===== SIDEBAR ===== */}
      <aside className="sidebar">
        <div className="sidebar__top">
          <div className="sidebar__brand">
            <h1>
              padre
              <br />
              bueno
            </h1>
            <span>GESTÃO DE RESTAURANTE</span>
          </div>

          <p className="sidebar__section">OPERAÇÃO</p>

          <nav className="sidebar__nav">
            {MENU.map((item) => (
              <a
                key={item.label}
                href="#"
                className={`sidebar__link ${item.ativo ? "sidebar__link--active" : ""}`}
              >
                <Icon name={item.icon} />
                {item.label}
              </a>
            ))}
          </nav>
        </div>

        <div className="sidebar__footer">
  <strong>Restaurante e Cantina Padre Bueno</strong>
  <span>Administrador</span>
  <a href="/login" onClick={handleLogout}>
    Sair da conta
  </a>
</div>
      </aside>

      {/* ===== CONTEÚDO ===== */}
      <div className="usuarios-main">
        <header className="topbar">
          <span className="topbar__breadcrumb">Operação / Usuários</span>

          <div className="topbar__right">
            <span className="topbar__sync">
              <i className="topbar__dot" />
              {carregando ? "Sincronizando..." : "Sincronizado agora"}
            </span>
            <div className="topbar__user">
              <strong>Marina Costa</strong>
              <span>Administrador</span>
            </div>
          </div>
        </header>

        <main className="usuarios-content">
          {/* Banner */}
          <section className="banner">
            <h4>Equipe e permissões</h4>
            <p>Um usuário, um perfil. Administradores com o mesmo acesso.</p>
          </section>

          {/* Abas */}
          <div className="tabs">
            <div className="tabs__left">
              <button
                type="button"
                className={`tab ${abaAtiva === "todos" ? "tab--active" : ""}`}
                onClick={() => {
                  setAbaAtiva("todos");
                  setPagina(1);
                }}
              >
                Usuários · {usuarios.length}
              </button>
              <button
                type="button"
                className={`tab ${abaAtiva === "admins" ? "tab--active" : ""}`}
                onClick={() => {
                  setAbaAtiva("admins");
                  setFiltroPerfil("todos");
                  setPagina(1);
                }}
              >
                Administradores · {totalAdmins}
              </button>
            </div>
            <button
              type="button"
              className="tab tab--novo"
              onClick={iniciarNovoAdmin}
            >
              Novo administrador
            </button>
          </div>

          {/* Aviso */}
          <div className="aviso">
            <strong>
              Perfil fixo após o cadastro. Para trocar a função ou conceder acesso de
              administrador, crie um novo usuário.
            </strong>
            <span>
              Os privilégios são definidos pelo perfil, sem permissões avulsas e sem
              hierarquia entre administradores.
            </span>
          </div>

          {/* Lista + Formulário */}
          <div className="grid">
            {/* Lista */}
            <section className="card lista">
              <div className="lista__filtros">
                <input
                  type="text"
                  className="input"
                  placeholder="Buscar por nome ou e-mail"
                  value={busca}
                  onChange={(e) => {
                    setBusca(e.target.value);
                    setPagina(1);
                  }}
                />
                <select
                  className="input"
                  value={filtroPerfil}
                  disabled={abaAtiva === "admins"}
                  onChange={(e) => {
                    setFiltroPerfil(e.target.value);
                    setPagina(1);
                  }}
                >
                  <option value="todos">Perfil: todos</option>
                  {PERFIS.map((p) => (
                    <option key={p.value} value={p.value}>
                      {p.label}
                    </option>
                  ))}
                </select>
              </div>

              <table className="tabela">
                <thead>
                  <tr>
                    <th>Nome / e-mail</th>
                    <th>Perfil</th>
                    <th>Ações</th>
                    <th />
                  </tr>
                </thead>
                <tbody>
                  {carregando && (
                    <tr>
                      <td colSpan="4" className="tabela__vazio">
                        Carregando usuários...
                      </td>
                    </tr>
                  )}

                  {!carregando && erroLista && (
                    <tr>
                      <td colSpan="4" className="tabela__vazio">
                        {erroLista}
                      </td>
                    </tr>
                  )}

                  {!carregando &&
                    !erroLista &&
                    visiveis.map((u) => {
                      const p = perfilInfo(u.perfil);
                      return (
                        <tr key={u.id}>
                          <td>
                            <strong
                              className="tabela__nome tabela__nome--clicavel"
                              onClick={() => abrirModalUsuario("visualizar", u)}
                            >
                              {u.nome}
                            </strong>
                            <span className="tabela__email">{u.email}</span>
                          </td>
                          <td>
                            <span className={`badge badge--${p.cor}`}>{p.label}</span>
                          </td>
                          <td>
                            <button
                              type="button"
                              className="acao acao--editar"
                              onClick={() => abrirModalUsuario("editar", u)}
                            >
                              Editar
                            </button>
                          </td>
                          <td>
                            <button
                              type="button"
                              className="acao acao--excluir"
                              onClick={() => abrirModalUsuario("excluir", u)}
                            >
                              Excluir
                            </button>
                          </td>
                        </tr>
                      );
                    })}

                  {!carregando && !erroLista && visiveis.length === 0 && (
                    <tr>
                      <td colSpan="4" className="tabela__vazio">
                        Nenhum usuário encontrado.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>

              <div className="paginacao">
                <span>
                  Exibindo {visiveis.length} de {filtrados.length} usuários
                </span>
                <div className="paginacao__controles">
                  <button
                    type="button"
                    className="paginacao__proxima"
                    disabled={paginaAtual <= 1}
                    onClick={() => setPagina(paginaAtual - 1)}
                  >
                    ← Anterior
                  </button>
                  <span>
                    {paginaAtual} / {totalPaginas}
                  </span>
                  <button
                    type="button"
                    className="paginacao__proxima"
                    disabled={paginaAtual >= totalPaginas}
                    onClick={() => setPagina(paginaAtual + 1)}
                  >
                    Próxima →
                  </button>
                </div>
              </div>
            </section>

            {/* Formulário */}
            <form className="card form" onSubmit={handleSubmitNovo} noValidate>
              <h3>Novo usuário</h3>

              <label className="campo">
                <span>Nome completo (mín. 8 caracteres)</span>
                <input
                  type="text"
                  name="nome"
                  className="input"
                  placeholder="Digite o nome completo"
                  value={form.nome}
                  onChange={handleChange}
                />
              </label>

              <label className="campo">
                <span>Nome de usuário / Login (mín. 8 caracteres)</span>
                <input
                  type="text"
                  name="nomeUsuario"
                  className="input"
                  placeholder="ex: marina.costa"
                  value={form.nomeUsuario}
                  onChange={handleChange}
                />
              </label>

              <label className="campo">
                <span>CPF</span>
                <input
                  type="text"
                  name="cpf"
                  className="input"
                  placeholder="000.000.000-00"
                  value={form.cpf}
                  onChange={handleChange}
                />
              </label>

              <label className="campo">
                <span>E-mail</span>
                <input
                  type="email"
                  name="email"
                  className="input"
                  placeholder="nome@padrebueno.com"
                  value={form.email}
                  onChange={handleChange}
                />
              </label>

              <label className="campo">
                <span>Senha</span>
                <input
                  type="password"
                  name="senha"
                  className="input"
                  placeholder="Ex: Senha@123"
                  value={form.senha}
                  onChange={handleChange}
                />
              </label>

              <label className="campo">
                <span>Perfil de acesso · não poderá ser alterado</span>
                <select
                  name="perfil"
                  className="input"
                  value={form.perfil}
                  onChange={handleChange}
                >
                  {PERFIS.map((p) => (
                    <option key={p.value} value={p.value}>
                      {p.label}
                    </option>
                  ))}
                </select>
              </label>

              <button type="submit" className="btn-criar" disabled={salvando}>
                {salvando ? "Criando..." : "Criar usuário"}
              </button>
            </form>
          </div>

          <p className="rodape-nota">
            Na edição: nome, CPF, e-mail e senha podem mudar. O perfil permanece
            somente leitura.
          </p>
        </main>
      </div>
    </div>
  );
}