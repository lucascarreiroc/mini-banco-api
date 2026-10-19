package com.lucascarreiro.minibanco.transferencia;

import com.lucascarreiro.minibanco.cliente.Cliente;
import com.lucascarreiro.minibanco.conta.Conta;
import com.lucascarreiro.minibanco.conta.ContaRepository;
import com.lucascarreiro.minibanco.shared.exception.RecursoNaoEncontradoException;
import com.lucascarreiro.minibanco.shared.exception.RegraNegocioException;
import com.lucascarreiro.minibanco.shared.exception.SaldoInsuficienteException;
import com.lucascarreiro.minibanco.transacao.TipoTransacao;
import com.lucascarreiro.minibanco.transacao.Transacao;
import com.lucascarreiro.minibanco.transacao.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferenciaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private TransferenciaService service;

    private Conta origem;
    private Conta destino;

    @BeforeEach
    void setUp() {
        origem = new Conta("00000001", new Cliente("Maria Silva", "52998224725", "maria@email.com", "hash-qualquer"));
        destino = new Conta("00000002", new Cliente("João Souza", "11144477735", "joao@email.com", "hash-qualquer"));
        origem.depositar(new BigDecimal("100.00"));
    }

    @Test
    void deveTransferirEntreContas() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(origem));
        when(contaRepository.findById(2L)).thenReturn(Optional.of(destino));

        TransferenciaResponse resposta = service.transferir(
                new TransferenciaRequest(1L, 2L, new BigDecimal("40.00")));

        assertThat(origem.getSaldo()).isEqualByComparingTo("60.00");
        assertThat(destino.getSaldo()).isEqualByComparingTo("40.00");
        assertThat(resposta.saldoAtualOrigem()).isEqualByComparingTo("60.00");

        ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
        verify(transacaoRepository, times(2)).save(captor.capture());

        List<Transacao> transacoes = captor.getAllValues();
        assertThat(transacoes.get(0).getTipo()).isEqualTo(TipoTransacao.TRANSFERENCIA_ENVIADA);
        assertThat(transacoes.get(0).getConta()).isSameAs(origem);
        assertThat(transacoes.get(0).getContaContraparte()).isSameAs(destino);
        assertThat(transacoes.get(1).getTipo()).isEqualTo(TipoTransacao.TRANSFERENCIA_RECEBIDA);
        assertThat(transacoes.get(1).getConta()).isSameAs(destino);
        assertThat(transacoes.get(1).getContaContraparte()).isSameAs(origem);
    }

    @Test
    void naoDeveTransferirParaMesmaConta() {
        assertThatThrownBy(() -> service.transferir(
                new TransferenciaRequest(1L, 1L, new BigDecimal("10.00"))))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("mesma conta");

        verifyNoInteractions(contaRepository, transacaoRepository);
    }

    @Test
    void naoDeveTransferirComSaldoInsuficiente() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(origem));
        when(contaRepository.findById(2L)).thenReturn(Optional.of(destino));

        assertThatThrownBy(() -> service.transferir(
                new TransferenciaRequest(1L, 2L, new BigDecimal("100.01"))))
                .isInstanceOf(SaldoInsuficienteException.class);

        assertThat(origem.getSaldo()).isEqualByComparingTo("100.00");
        assertThat(destino.getSaldo()).isEqualByComparingTo("0");
        verify(transacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveTransferirParaContaInexistente() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(origem));
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.transferir(
                new TransferenciaRequest(1L, 99L, new BigDecimal("10.00"))))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");

        assertThat(origem.getSaldo()).isEqualByComparingTo("100.00");
        verify(transacaoRepository, never()).save(any());
    }
}