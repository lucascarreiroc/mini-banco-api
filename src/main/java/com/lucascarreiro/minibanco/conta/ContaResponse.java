package com.lucascarreiro.minibanco.conta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContaResponse(
        Long id,
        String numero,
        BigDecimal saldo,
        Long clienteId,
        String titular,
        LocalDateTime criadaEm
) {
    public static ContaResponse de(Conta conta) {
        return new ContaResponse(
                conta.getId(), conta.getNumero(), conta.getSaldo(),
                conta.getCliente().getId(), conta.getCliente().getNome(), conta.getCriadaEm());
    }
}
