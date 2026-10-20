package com.lucascarreiro.minibanco.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String cpf,
        @NotBlank String senha
) {
    public String cpfSomenteNumeros() {
        return cpf.replaceAll("\\D", "");
    }
}