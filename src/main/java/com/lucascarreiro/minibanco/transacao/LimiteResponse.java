package com.lucascarreiro.minibanco.transacao;

import java.math.BigDecimal;

public record LimiteResponse(BigDecimal limiteDiario, BigDecimal utilizadoHoje, BigDecimal disponivelHoje) {
}