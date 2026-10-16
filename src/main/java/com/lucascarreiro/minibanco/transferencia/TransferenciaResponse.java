package com.lucascarreiro.minibanco.transferencia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenciaResponse(
        Long contaOrigemId,
        Long contaDestinoId,
        BigDecimal valor,
        BigDecimal saldoAtualOrigem,
        LocalDateTime dataHora
) {
}