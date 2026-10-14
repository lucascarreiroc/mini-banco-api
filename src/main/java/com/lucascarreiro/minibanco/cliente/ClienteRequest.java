package com.lucascarreiro.minibanco.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record ClienteRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @CPF(message = "CPF inválido") String cpf,
        @NotBlank @Email String email
) {
    /** Remove pontos e traço para guardar só os 11 dígitos. */
    public String cpfSomenteNumeros() {
        return cpf.replaceAll("\\D", "");
    }
}
