package br.ufla.inovatech.service;

import br.ufla.inovatech.dto.UsuarioTokenDTO;
import br.ufla.inovatech.dto.usuario.AlterarSenhaRequest;
import br.ufla.inovatech.dto.usuario.AtualizaRequest;
import br.ufla.inovatech.dto.usuario.AuthRequest;
import br.ufla.inovatech.dto.usuario.LerResponse;
import br.ufla.inovatech.dto.usuario.RegisterRequest;
import br.ufla.inovatech.model.Usuario;
import br.ufla.inovatech.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

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

        return tokenService.gerarToken(usuario.getEmail(), usuario.getCpf(), usuario.getPerfil());
    }

    public String auth(@RequestBody AuthRequest request){
        Usuario usuario = usuarioRepository.findByEmail(request.email());
        if (usuario == null || !passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
        }
        return tokenService.gerarToken(usuario.getEmail(), usuario.getCpf(), usuario.getPerfil());
    }

    public Usuario consultarId(Integer id){
        return usuarioRepository.findById(id).orElse(null);
    }

    /** Consulta um usuário pelo id, sem expor a senha. Retorna null se não existir. */
    public LerResponse consultarPorId(Integer id){
        Usuario usuario = consultarId(id);
        return usuario != null ? paraResponse(usuario) : null;
    }

    private LerResponse paraResponse(Usuario u){
        return new LerResponse(u.getId(), u.getEmail(), u.getNome(), u.getUserName(), u.getCpf(), u.getPerfil());
    }

    public boolean deletar(Integer id){
        Usuario usuario = consultarId(id);
        if (usuario == null) return false;

        // RN13: o sistema deve ter sempre pelo menos um Administrador cadastrado.
        if ("ADMINISTRADOR".equals(usuario.getPerfil())
                && usuarioRepository.countByPerfil("ADMINISTRADOR") <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir o último Administrador do sistema");
        }

        usuarioRepository.delete(usuario);
        return true;
    }

    public LerResponse atualizar(Integer id, AtualizaRequest request){
        Usuario usuario = consultarId(id);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        if (usuarioRepository.existsByCpfAndIdNot(request.cpf(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já vinculado a outro usuário");
        }
        if (usuarioRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já vinculado a outro usuário");
        }

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setCpf(request.cpf());
        usuarioRepository.save(usuario);

        return paraResponse(usuario);
    }

    public void alterarSenha(Integer id, AlterarSenhaRequest request, UsuarioTokenDTO solicitante){
        Usuario usuario = consultarId(id);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }

        boolean ehAdministrador = "ADMINISTRADOR".equals(solicitante.perfil());
        if (!ehAdministrador) {
            if (!usuario.getEmail().equals(solicitante.email())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode alterar a sua própria senha");
            }
            if (request.senhaAtual() == null || !passwordEncoder.matches(request.senhaAtual(), usuario.getSenha())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
            }
        }

        if (!request.novaSenha().equals(request.confirmarSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A confirmação é diferente da nova senha");
        }

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);
    }

    public HttpStatus validarPermissao(String token){
        if (!tokenService.isValido(token)) return HttpStatus.UNAUTHORIZED;
        if (!"ADMINISTRADOR".equals(tokenService.extrairPerfil(token))) return HttpStatus.FORBIDDEN;
        return HttpStatus.OK;
    }

    public List<LerResponse> consultarTodos(){
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<LerResponse> responses = new ArrayList<>();
        for(Usuario u : usuarios){
            responses.add(paraResponse(u));
        }
        return responses;
    }

}
