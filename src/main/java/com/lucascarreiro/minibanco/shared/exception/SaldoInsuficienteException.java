package com.lucascarreiro.minibanco.shared.exception;

public class SaldoInsuficienteException extends RegraNegocioException {
    public SaldoInsuficienteException() {
        super("Saldo insuficiente para realizar a operação");
    }
}
