package com.lucascarreiro.minibanco.transacao;

import com.lucascarreiro.minibanco.cliente.Cliente;
import com.lucascarreiro.minibanco.conta.Conta;
import com.lucascarreiro.minibanco.conta.ContaRepository;
import com.lucascarreiro.minibanco.shared.exception.AcessoNegadoException;
import com.lucascarreiro.minibanco.shared.exception.RecursoNaoEncontradoException;
import com.lucascarreiro.minibanco.shared.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtratoServiceTest {

    private static final Long CLIENTE_ID = 1L;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ExtratoService service;

    private final Pageable pageable = PageRequest.of(0, 20);

    private Conta conta;

    @BeforeEach
    void setUp() {
        Cliente cliente = new Cliente("Maria Silva", "52998224725", "maria@email.com", "hash-qualquer");
        ReflectionTestUtils.setField(cliente, "id", CLIENTE_ID);
        conta = new Conta("00000001", cliente);
    }

    @Test
    void deveLancarExcecaoQuandoContaNaoExiste() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultar(99L, null, null, pageable, CLIENTE_ID))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");

        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void naoDevePermitirExtratoDeContaDeOutroCliente() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        assertThatThrownBy(() -> service.consultar(1L, null, null, pageable, 2L))
                .isInstanceOf(AcessoNegadoException.class);

        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void deveRejeitarDataInicialPosteriorAFinal() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        assertThatThrownBy(() -> service.consultar(1L,
                LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 10), pageable, CLIENTE_ID))
                .isInstanceOf(RegraNegocioException.class);

        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void deveUsarUltimos30DiasQuandoDatasNaoInformadas() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(transacaoRepository.findByContaIdAndDataHoraBetween(eq(1L), any(), any(), eq(pageable)))
                .thenReturn(Page.empty());

        service.consultar(1L, null, null, pageable, CLIENTE_ID);

        ArgumentCaptor<LocalDateTime> inicio = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> fim = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(transacaoRepository).findByContaIdAndDataHoraBetween(
                eq(1L), inicio.capture(), fim.capture(), eq(pageable));

        LocalDate hoje = LocalDate.now();
        assertThat(inicio.getValue()).isEqualTo(hoje.minusDays(30).atStartOfDay());
        assertThat(fim.getValue()).isEqualTo(hoje.atTime(LocalTime.MAX));
    }

    @Test
    void deveConverterTransacoesEmItensDoExtrato() {
        conta.depositar(new BigDecimal("150.00"));
        Transacao deposito = new Transacao(conta, TipoTransacao.DEPOSITO, new BigDecimal("150.00"), null);

        LocalDate dataInicio = LocalDate.of(2026, 10, 1);
        LocalDate dataFim = LocalDate.of(2026, 10, 15);

        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(transacaoRepository.findByContaIdAndDataHoraBetween(
                1L, dataInicio.atStartOfDay(), dataFim.atTime(LocalTime.MAX), pageable))
                .thenReturn(new PageImpl<>(List.of(deposito), pageable, 1));

        Page<ExtratoItemResponse> resultado = service.consultar(1L, dataInicio, dataFim, pageable, CLIENTE_ID);

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        ExtratoItemResponse item = resultado.getContent().get(0);
        assertThat(item.tipo()).isEqualTo(TipoTransacao.DEPOSITO);
        assertThat(item.valor()).isEqualByComparingTo("150.00");
        assertThat(item.saldoApos()).isEqualByComparingTo("150.00");
        assertThat(item.contaContraparte()).isNull();
    }
}