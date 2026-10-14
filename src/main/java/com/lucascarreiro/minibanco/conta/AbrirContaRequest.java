package com.lucascarreiro.minibanco.conta;

import jakarta.validation.constraints.NotNull;

public record AbrirContaRequest(@NotNull Long clienteId) {
}
