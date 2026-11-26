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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
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
