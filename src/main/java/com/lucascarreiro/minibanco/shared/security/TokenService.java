package com.lucascarreiro.minibanco.shared.security;

import com.lucascarreiro.minibanco.cliente.Cliente;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long expiracaoMinutos;

    public TokenService(JwtEncoder jwtEncoder, @Value("${jwt.expiracao-minutos}") long expiracaoMinutos) {
        this.jwtEncoder = jwtEncoder;
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public String gerarToken(Cliente cliente) {
        Instant agora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("mini-banco")
                .subject(cliente.getId().toString())
                .issuedAt(agora)
                .expiresAt(agora.plus(expiracaoMinutos, ChronoUnit.MINUTES))
                .claim("nome", cliente.getNome())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getExpiracaoEmSegundos() {
        return expiracaoMinutos * 60;
    }
}