package com.lucascarreiro.minibanco.conta;

import com.lucascarreiro.minibanco.cliente.Cliente;
import com.lucascarreiro.minibanco.cliente.ClienteRepository;
import com.lucascarreiro.minibanco.shared.exception.AcessoNegadoException;
import com.lucascarreiro.minibanco.shared.exception.RecursoNaoEncontradoException;
import com.lucascarreiro.minibanco.transacao.LimiteDiarioService;
import com.lucascarreiro.minibanco.transacao.Transacao;
import com.lucascarreiro.minibanco.transacao.TransacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private LimiteDiarioService limiteDiarioService;

    @InjectMocks
    private ContaService service;


    @Test
    void deveLancarExcecaoQuandoContaNaoExiste() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.depositar(99L, BigDecimal.TEN, 1L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveDepositarNaContaDoProprioCliente() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(criarContaDoCliente(1L)));

        ContaResponse resposta = service.depositar(1L, new BigDecimal("200.00"), 1L);

        assertThat(resposta.saldo()).isEqualByComparingTo("200.00");
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    void naoDevePermitirDepositoNaContaDeOutroCliente() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(criarContaDoCliente(1L)));

        assertThatThrownBy(() -> service.depositar(1L, new BigDecimal("200.00"), 2L))
                .isInstanceOf(AcessoNegadoException.class);

        verifyNoInteractions(transacaoRepository);
    }

    private Conta criarContaDoCliente(Long clienteId) {
        Cliente cliente = new Cliente("Maria Silva", "52998224725", "maria@email.com", "hash-qualquer");
        ReflectionTestUtils.setField(cliente, "id", clienteId);
        return new Conta("00000001", cliente);
    }
}