package com.lucascarreiro.minibanco.shared.exception;

public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        super("CPF ou senha inválidos");
    }
}