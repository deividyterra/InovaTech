package br.ufla.inovatech.service.entity;

import br.ufla.inovatech.testeCaixaPreta.EntidadeTestes;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public class Usuario extends EntidadeTestes{
    private String nome;
    private String email;
    private String senha;
    private String nomeUsuario;
    private String cpf;

    public Usuario(String nome,String email,String nomeUsuario,Integer id){
        super(id);
        this.email = email;
        this.nome = nome;
        this.nomeUsuario = nomeUsuario;
    };

    public Usuario(String nome,String email,String nomeUsuario,String senha){
        this.email = email;
        this.nome = nome;
        this.nomeUsuario = nomeUsuario;
        this.senha = senha;
    }

    public Usuario(Usuario data){
        this.email = data.getEmail();
        this.senha = data.getSenha();
        this.nomeUsuario = data.getNomeUsuario();
        this.nome = data.getNome();
        this.cpf = getCpf();
        super.setId(data.getId());
    }

    public Usuario(File data) {
        super();
        try {
            new ObjectMapper().readerForUpdating(this).readValue(data);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler " + data.getName(), e);
        }
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
}
