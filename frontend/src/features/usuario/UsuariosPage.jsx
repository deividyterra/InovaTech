import { useMemo, useState } from "react";
import "./UsuariosPage.css";

/* ========================
   DADOS MOCK (trocar por usuario.api.js)
======================== */

const PERFIS = [
  { value: "garcom", label: "Garçom", cor: "yellow" },
  { value: "cozinheiro", label: "Cozinheiro", cor: "green" },
  { value: "gestor", label: "Gestor de pedidos", cor: "blue" },
  { value: "caixa", label: "Caixa", cor: "purple" },
  { value: "admin", label: "Administrador", cor: "red" },
];

const BASE = [
  { nome: "Clara Nunes", email: "clara@oliveiras.com", perfil: "garcom" },
  { nome: "Beatriz Oliveira", email: "beatriz@oliveiras.com", perfil: "cozinheiro" },
  { nome: "Rafael Mendes", email: "rafael@oliveiras.com", perfil: "gestor" },
  { nome: "Lucas Santos", email: "lucas@oliveiras.com", perfil: "caixa" },
  { nome: "Marina Costa", email: "marina@oliveiras.com", perfil: "admin" },
];

const USUARIOS_MOCK = Array.from({ length: 30 }, (_, i) => ({
  ...BASE[i % 5],
  id: i + 1,
}));

const POR_PAGINA = 5;

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

function mascararCpf(valor) {
  const n = valor.replace(/\D/g, "").slice(0, 11);
  return n
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}

const perfilInfo = (valor) => PERFIS.find((p) => p.value === valor);

/* ========================
   PÁGINA
======================== */

export default function UsuariosPage() {
  const [usuarios] = useState(USUARIOS_MOCK);
  const [busca, setBusca] = useState("");
  const [filtroPerfil, setFiltroPerfil] = useState("todos");
  const [pagina, setPagina] = useState(1);

  const [form, setForm] = useState({
    nome: "",
    cpf: "",
    email: "",
    senha: "",
    perfil: "garcom",
  });

  const filtrados = useMemo(() => {
    const termo = busca.trim().toLowerCase();
    return usuarios.filter((u) => {
      const bateBusca =
        !termo ||
        u.nome.toLowerCase().includes(termo) ||
        u.email.toLowerCase().includes(termo);
      const batePerfil = filtroPerfil === "todos" || u.perfil === filtroPerfil;
      return bateBusca && batePerfil;
    });
  }, [usuarios, busca, filtroPerfil]);

  const totalPaginas = Math.max(1, Math.ceil(filtrados.length / POR_PAGINA));
  const paginaAtual = Math.min(pagina, totalPaginas);
  const visiveis = filtrados.slice(
    (paginaAtual - 1) * POR_PAGINA,
    paginaAtual * POR_PAGINA
  );
  const totalAdmins = usuarios.filter((u) => u.perfil === "admin").length;

  function handleChange(e) {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: name === "cpf" ? mascararCpf(value) : value }));
  }

  function handleSubmit(e) {
    e.preventDefault();
    // TODO: chamar usuario.api.js (criarUsuario)
    console.log("Criar usuário:", form);
  }

  return (
    <div className="usuarios-layout">
      {/* ===== SIDEBAR (mover para shared/components/layout depois) ===== */}
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
          <a href="#">Sair da conta</a>
        </div>
      </aside>

      {/* ===== CONTEÚDO ===== */}
      <div className="usuarios-main">
        {/* Topbar (mover para Header depois) */}
        <header className="topbar">
          <span className="topbar__breadcrumb">Operação / Usuários</span>

          <div className="topbar__right">
            <span className="topbar__sync">
              <i className="topbar__dot" />
              Sincronizado agora
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
              <button type="button" className="tab tab--active">
                Usuários · {usuarios.length}
              </button>
              <button type="button" className="tab">
                Administradores · {totalAdmins}
              </button>
            </div>
            <button type="button" className="tab tab--novo">
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
                  {visiveis.map((u) => {
                    const p = perfilInfo(u.perfil);
                    return (
                      <tr key={u.id}>
                        <td>
                          <strong className="tabela__nome">{u.nome}</strong>
                          <span className="tabela__email">{u.email}</span>
                        </td>
                        <td>
                          <span className={`badge badge--${p.cor}`}>{p.label}</span>
                        </td>
                        <td>
                          <button type="button" className="acao acao--editar">
                            Editar
                          </button>
                        </td>
                        <td>
                          <button type="button" className="acao acao--excluir">
                            Excluir
                          </button>
                        </td>
                      </tr>
                    );
                  })}

                  {visiveis.length === 0 && (
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
            <form className="card form" onSubmit={handleSubmit}>
              <h3>Novo usuário</h3>

              <label className="campo">
                <span>Nome completo</span>
                <input
                  type="text"
                  name="nome"
                  className="input"
                  placeholder="Digite o nome"
                  value={form.nome}
                  onChange={handleChange}
                  required
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
                  required
                />
              </label>

              <label className="campo">
                <span>E-mail</span>
                <input
                  type="email"
                  name="email"
                  className="input"
                  placeholder="nome@oliveiras.com"
                  value={form.email}
                  onChange={handleChange}
                  required
                />
              </label>

              <label className="campo">
                <span>Senha</span>
                <input
                  type="password"
                  name="senha"
                  className="input"
                  placeholder="••••••••••••"
                  minLength={8}
                  value={form.senha}
                  onChange={handleChange}
                  required
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

              <button type="submit" className="btn-criar">
                Criar usuário
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
