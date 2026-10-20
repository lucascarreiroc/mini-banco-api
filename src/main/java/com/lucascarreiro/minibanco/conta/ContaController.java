package com.lucascarreiro.minibanco.conta;

import com.lucascarreiro.minibanco.shared.security.UsuarioAutenticado;
import com.lucascarreiro.minibanco.transacao.LimiteResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ContaResponse> abrir(@AuthenticationPrincipal Jwt jwt) {
        ContaResponse criada = service.abrir(UsuarioAutenticado.id(jwt));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    public List<ContaResponse> listarMinhasContas(@AuthenticationPrincipal Jwt jwt) {
        return service.listarDoCliente(UsuarioAutenticado.id(jwt));
    }

    @GetMapping("/{id}")
    public ContaResponse buscar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.buscarPorId(id, UsuarioAutenticado.id(jwt));
    }

    @PostMapping("/{id}/deposito")
    public ContaResponse depositar(@PathVariable Long id, @RequestBody @Valid ValorRequest request,
                                   @AuthenticationPrincipal Jwt jwt) {
        return service.depositar(id, request.valor(), UsuarioAutenticado.id(jwt));
    }

    @PostMapping("/{id}/saque")
    public ContaResponse sacar(@PathVariable Long id, @RequestBody @Valid ValorRequest request,
                               @AuthenticationPrincipal Jwt jwt) {
        return service.sacar(id, request.valor(), UsuarioAutenticado.id(jwt));
    }

    @GetMapping("/{id}/limite")
    public LimiteResponse consultarLimite(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.consultarLimite(id, UsuarioAutenticado.id(jwt));
    }
}
