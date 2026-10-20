package com.lucascarreiro.minibanco.transacao;

import com.lucascarreiro.minibanco.shared.security.UsuarioAutenticado;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/contas/{contaId}/extrato")
public class ExtratoController {

    private final ExtratoService service;

    public ExtratoController(ExtratoService service) {
        this.service = service;
    }

    @GetMapping
    public Page<ExtratoItemResponse> consultar(
            @PathVariable Long contaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @ParameterObject @PageableDefault(size = 20, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        return service.consultar(contaId, dataInicio, dataFim, pageable, UsuarioAutenticado.id(jwt));
    }
}