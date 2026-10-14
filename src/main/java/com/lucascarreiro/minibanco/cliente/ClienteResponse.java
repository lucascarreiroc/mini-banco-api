package com.lucascarreiro.minibanco.cliente;

import java.time.LocalDateTime;

public record ClienteResponse(Long id, String nome, String cpf, String email, LocalDateTime criadoEm) {

    public static ClienteResponse de(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(), cliente.getNome(), cliente.getCpf(),
                cliente.getEmail(), cliente.getCriadoEm());
    }
}
