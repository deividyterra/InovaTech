package br.ufla.inovatech.controller;

import br.ufla.inovatech.dto.AuthRequest;
import br.ufla.inovatech.dto.AuthResponse;
import br.ufla.inovatech.dto.RegisterRequest;
import br.ufla.inovatech.service.TokenService;
import br.ufla.inovatech.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

}
