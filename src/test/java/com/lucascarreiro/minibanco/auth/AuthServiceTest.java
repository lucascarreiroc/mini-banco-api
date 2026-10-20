package com.lucascarreiro.minibanco.auth;

import com.lucascarreiro.minibanco.cliente.Cliente;
import com.lucascarreiro.minibanco.cliente.ClienteRepository;
import com.lucascarreiro.minibanco.shared.exception.CredenciaisInvalidasException;
import com.lucascarreiro.minibanco.shared.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String CPF_LIMPO = "52998224725";
    private static final String HASH_SALVO = "$2a$10$hash-guardado-no-banco";

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("Maria Silva", CPF_LIMPO, "maria@email.com", HASH_SALVO);
    }

    @Test
    void deveRetornarTokenQuandoCredenciaisSaoValidas() {
        when(clienteRepository.findByCpf(CPF_LIMPO)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("senhaMaria123", HASH_SALVO)).thenReturn(true);
        when(tokenService.gerarToken(cliente)).thenReturn("token-gerado");
        when(tokenService.getExpiracaoEmSegundos()).thenReturn(3600L);

        TokenResponse resposta = service.login(new LoginRequest("529.982.247-25", "senhaMaria123"));

        assertThat(resposta.token()).isEqualTo("token-gerado");
        assertThat(resposta.tipo()).isEqualTo("Bearer");
        assertThat(resposta.expiraEmSegundos()).isEqualTo(3600L);
    }

    @Test
    void deveRejeitarCpfInexistente() {
        when(clienteRepository.findByCpf(CPF_LIMPO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequest(CPF_LIMPO, "qualquerSenha")))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("CPF ou senha inválidos");

        verifyNoInteractions(passwordEncoder, tokenService);
    }

    @Test
    void deveRejeitarSenhaIncorreta() {
        when(clienteRepository.findByCpf(CPF_LIMPO)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("senhaErrada", HASH_SALVO)).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginRequest(CPF_LIMPO, "senhaErrada")))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("CPF ou senha inválidos");

        verify(tokenService, never()).gerarToken(any());
    }
}