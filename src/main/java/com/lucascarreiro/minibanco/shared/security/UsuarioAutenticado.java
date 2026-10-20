package com.lucascarreiro.minibanco.shared.security;

import org.springframework.security.oauth2.jwt.Jwt;

public final class UsuarioAutenticado {

    private UsuarioAutenticado() {
    }

    public static Long id(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}