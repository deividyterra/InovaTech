package br.ufla.inovatech.controller;

import br.ufla.inovatech.dto.usuario.*;
import br.ufla.inovatech.service.Sanitizer;
import br.ufla.inovatech.service.TokenService;
import br.ufla.inovatech.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public UsuarioController(TokenService tokenService, UsuarioService usuarioService) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<TokenResponse> cadastrar(@RequestBody @Valid CadastroRequest request){
        String token = usuarioService.cadastrar(request);
        return ResponseEntity.ok(new TokenResponse(token,"Cadastro realizado com sucesso"));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request){
        String token = usuarioService.autenticar(request);
        return ResponseEntity.ok(new TokenResponse(token,"Login realizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Integer id){
        HttpStatus status = usuarioService.validarPermissao(Sanitizer.token(authorization));
        if (status != HttpStatus.OK) {
            return ResponseEntity.status(status).build();
        }
        return usuarioService.deletar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LerResponse> consultarPorId(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Integer id){
        HttpStatus status = usuarioService.validarPermissao(Sanitizer.token(authorization));
        if (status != HttpStatus.OK) {
            return ResponseEntity.status(status).build();
        }
        LerResponse usuario = usuarioService.consultarPorId(id);
        return usuario != null
                ? ResponseEntity.ok(usuario)
                : ResponseEntity.notFound().build();
    }

    /** RF-005.3: o Administrador edita nome, e-mail e CPF. Login e Perfil não podem ser alterados. */
    @PutMapping("/{id}")
    public ResponseEntity<LerResponse> atualizar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Integer id,
            @RequestBody @Valid AtualizaRequest request){
        HttpStatus status = usuarioService.validarPermissao(Sanitizer.token(authorization));
        if (status != HttpStatus.OK) {
            return ResponseEntity.status(status).build();
        }
        return ResponseEntity.ok(usuarioService.atualizar(id, request));
    }

    /**
     * RF-002: troca de senha.
     * O próprio Usuário precisa informar a senha atual; o Administrador pode alterar a de qualquer um sem ela.
     */
    @PutMapping("/{id}/senha")
    public ResponseEntity<Void> alterarSenha(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Integer id,
            @RequestBody @Valid AlterarSenhaRequest request){
        String token = Sanitizer.token(authorization);
        if (!tokenService.tokenValido(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        usuarioService.alterarSenha(id, request, tokenService.extrairUsuario(token));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<LerResponse>> consultarTodos(
            @RequestHeader(value = "Authorization", required = false) String authorization){
        HttpStatus status = usuarioService.validarPermissao(Sanitizer.token(authorization));
        if(status != HttpStatus.OK) {
            return ResponseEntity.status(status).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.consultarTodos());
    }

}
