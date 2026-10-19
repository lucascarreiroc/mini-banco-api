package com.lucascarreiro.minibanco.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record ClienteRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @CPF(message = "CPF inválido") String cpf,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres") String senha

) {
    /**
     * Remove pontos e traço para guardar só os 11 dígitos.
     */
    public String cpfSomenteNumeros() {
        return cpf.replaceAll("\\D", "");
    }
}
