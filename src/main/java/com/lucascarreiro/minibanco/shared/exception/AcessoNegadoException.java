package com.lucascarreiro.minibanco.shared.exception;

public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException() {
        super("Você não tem permissão para acessar esta conta");
    }
}