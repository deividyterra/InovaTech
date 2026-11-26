package br.ufla.inovatech.dto.usuario;

public record LerResponse (
    Integer id,
    String email,
    String nome,
    String nomeUsuario,
    String cpf,
    String perfil
){}
