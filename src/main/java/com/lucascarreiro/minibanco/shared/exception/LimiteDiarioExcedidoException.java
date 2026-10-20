package com.lucascarreiro.minibanco.shared.exception;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LimiteDiarioExcedidoException extends RegraNegocioException {
    public LimiteDiarioExcedidoException(BigDecimal disponivel) {
        super("Limite diário excedido. Disponível para hoje: R$ " + disponivel.setScale(2, RoundingMode.HALF_EVEN));
    }
}