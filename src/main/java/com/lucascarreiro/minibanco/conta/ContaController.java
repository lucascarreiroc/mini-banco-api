package com.lucascarreiro.minibanco.conta;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ContaResponse> abrir(@RequestBody @Valid AbrirContaRequest request) {
        ContaResponse criada = service.abrir(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping("/{id}")
    public ContaResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping("/{id}/deposito")
    public ContaResponse depositar(@PathVariable Long id, @RequestBody @Valid ValorRequest request) {
        return service.depositar(id, request.valor());
    }

    @PostMapping("/{id}/saque")
    public ContaResponse sacar(@PathVariable Long id, @RequestBody @Valid ValorRequest request) {
        return service.sacar(id, request.valor());
    }
}
