const BASE_URL = "/api/usuario";

function getAuthHeaders() {
  const token = localStorage.getItem("token");
  return {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

async function tratarResposta(response) {
  if (response.status === 204) return null;

  const texto = await response.text();
  let dados = null;
  try {
    dados = texto ? JSON.parse(texto) : null;
  } catch {
    dados = texto;
  }

  if (!response.ok) {
    const mensagem =
      dados?.message ||
      dados?.erro ||
      (Array.isArray(dados?.errors) && dados.errors[0]?.defaultMessage) ||
      `Erro ${response.status} ao comunicar com o servidor.`;
    throw new Error(mensagem);
  }

  return dados;
}

// Normaliza o perfil vindo do Java (ex: "ADMIN" -> "admin") para bater com os badges do CSS
function normalizarUsuario(u) {
  return {
    ...u,
    perfil: (u.perfil || "garcom").toLowerCase(),
  };
}

export async function loginUsuario(credenciais) {
  const res = await fetch(`${BASE_URL}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(credenciais),
  });
  const data = await tratarResposta(res);
  if (data?.token) {
    localStorage.setItem("token", data.token);
  }
  return data;
}

export async function listarUsuarios() {
  const res = await fetch(BASE_URL, {
    method: "GET",
    headers: getAuthHeaders(),
  });
  const lista = await tratarResposta(res);
  return Array.isArray(lista) ? lista.map(normalizarUsuario) : [];
}

export async function cadastrarUsuario({ nome, nomeUsuario, email, senha, cpf, perfil }) {
  const res = await fetch(`${BASE_URL}/cadastro`, {
    method: "POST",
    headers: getAuthHeaders(),
    body: JSON.stringify({
      nome: nome.trim(),
      nomeUsuario: nomeUsuario.trim(),
      email: email.trim(),
      senha,
      cpf,
      perfil: (perfil || "GARCOM").toUpperCase(),
    }),
  });
  return tratarResposta(res);
}

export async function atualizarUsuario(id, { nome, email, cpf }) {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "PUT",
    headers: getAuthHeaders(),
    body: JSON.stringify({
      nome: nome.trim(),
      email: email.trim(),
      cpf,
    }),
  });
  const atualizado = await tratarResposta(res);
  return normalizarUsuario(atualizado);
}

export async function alterarSenhaUsuario(id, { novaSenha, confirmarSenha, senhaAtual = "" }) {
  const res = await fetch(`${BASE_URL}/${id}/senha`, {
    method: "PUT",
    headers: getAuthHeaders(),
    body: JSON.stringify({
      senhaAtual,
      novaSenha,
      confirmarSenha,
    }),
  });
  return tratarResposta(res);
}

export async function excluirUsuario(id) {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "DELETE",
    headers: getAuthHeaders(),
  });
  return tratarResposta(res);
}