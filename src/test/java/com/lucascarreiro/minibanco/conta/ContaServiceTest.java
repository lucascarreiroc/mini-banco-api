package com.lucascarreiro.minibanco.conta;

import com.lucascarreiro.minibanco.cliente.Cliente;
import com.lucascarreiro.minibanco.cliente.ClienteRepository;
import com.lucascarreiro.minibanco.shared.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ContaService service;

    @Test
    void deveLancarExcecaoQuandoContaNaoExiste() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.depositar(99L, BigDecimal.TEN))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveDepositarNaContaExistente() {
        Conta conta = new Conta("00000001", new Cliente("Maria Silva", "12345678909", "maria@email.com"));
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        ContaResponse resposta = service.depositar(1L, new BigDecimal("200.00"));

        assertThat(resposta.saldo()).isEqualByComparingTo("200.00");
    }
}
