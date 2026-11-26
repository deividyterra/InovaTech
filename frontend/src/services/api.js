import axios from 'axios';

const api = axios.create({
  // *Alterar a porta para a porta utilizada no back-end
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
});

// Interceptador: envia o token salvo no navegador em toda requisição autenticada
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('@InovaTech:token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;