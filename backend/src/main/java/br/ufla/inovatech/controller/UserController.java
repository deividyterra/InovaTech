package br.ufla.inovatech.controller;

import br.ufla.inovatech.dto.usuario.AuthRequest;
import br.ufla.inovatech.dto.usuario.AuthResponse;
import br.ufla.inovatech.dto.usuario.DeleteRequest;
import br.ufla.inovatech.dto.usuario.RegisterRequest;
import br.ufla.inovatech.service.TokenService;
import br.ufla.inovatech.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public UserController(TokenService tokenService, UsuarioService usuarioService) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request){
        String token = usuarioService.register(request);
        return ResponseEntity.ok(new AuthResponse(token,"Cadastro realizado com sucesso"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request){
        String token = usuarioService.auth(request);
        return ResponseEntity.ok(new AuthResponse(token,"Login realizado com sucesso"));
    }

    @DeleteMapping("/usuarios")
    public ResponseEntity<Void> delete(@RequestBody @Valid DeleteRequest request){
        HttpStatus status = usuarioService.validarPermissao(request.token());
        if (status != HttpStatus.OK) {
            return ResponseEntity.status(status).build();
        }
        return usuarioService.deletar(request.id())
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}
