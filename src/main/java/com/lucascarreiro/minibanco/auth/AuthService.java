package com.lucascarreiro.minibanco.auth;

import com.lucascarreiro.minibanco.cliente.Cliente;
import com.lucascarreiro.minibanco.cliente.ClienteRepository;
import com.lucascarreiro.minibanco.shared.exception.CredenciaisInvalidasException;
import com.lucascarreiro.minibanco.shared.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        Cliente cliente = clienteRepository.findByCpf(request.cpfSomenteNumeros())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(request.senha(), cliente.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        String token = tokenService.gerarToken(cliente);
        return TokenResponse.bearer(token, tokenService.getExpiracaoEmSegundos());
    }
}