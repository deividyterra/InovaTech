package br.ufla.inovatech.service;

import br.ufla.inovatech.dto.AuthRequest;
import br.ufla.inovatech.dto.RegisterRequest;
import br.ufla.inovatech.model.Usuario;
import br.ufla.inovatech.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UsuarioService {

    private PasswordEncoder passwordEncoder;
    private UsuarioRepository usuarioRepository;
    private TokenService tokenService;

    public UsuarioService(UsuarioRepository usuarioRepository,PasswordEncoder passwordEncoder,TokenService tokenService){
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    public String register(@RequestBody RegisterRequest request){

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setNome(request.nome());
        usuario.setUserName(request.nomeUsuario());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setCpf(request.cpf());
        usuario.setPerfil("GARCOM");
        usuarioRepository.save(usuario);

        return tokenService.gerarToken(usuario.getEmail(), usuario.getCpf());
    }

    public String auth(@RequestBody AuthRequest request){
        Usuario usuario = usuarioRepository.findByEmail(request.email());
        if (usuario == null || !passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
        }
        return tokenService.gerarToken(usuario.getEmail(), usuario.getCpf());
    }
}
