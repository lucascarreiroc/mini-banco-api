package com.lucascarreiro.minibanco.transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExtratoItemResponse(
        Long id,
        TipoTransacao tipo,
        BigDecimal valor,
        BigDecimal saldoApos,
        String contaContraparte,
        LocalDateTime dataHora
) {
    public static ExtratoItemResponse de(Transacao transacao) {
        String contraparte = transacao.getContaContraparte() == null
                ? null
                : transacao.getContaContraparte().getNumero();

        return new ExtratoItemResponse(
                transacao.getId(), transacao.getTipo(), transacao.getValor(),
                transacao.getSaldoApos(), contraparte, transacao.getDataHora());
    }
}