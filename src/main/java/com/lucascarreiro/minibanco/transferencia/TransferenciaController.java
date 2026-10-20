package com.lucascarreiro.minibanco.transferencia;

import com.lucascarreiro.minibanco.shared.security.UsuarioAutenticado;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transferencias")
public class TransferenciaController {

    private final TransferenciaService service;

    public TransferenciaController(TransferenciaService service) {
        this.service = service;
    }

    @PostMapping
    public TransferenciaResponse transferir(@RequestBody @Valid TransferenciaRequest request,
                                            @AuthenticationPrincipal Jwt jwt) {
        return service.transferir(request, UsuarioAutenticado.id(jwt));
    }
}