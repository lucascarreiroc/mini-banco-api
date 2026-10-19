package com.lucascarreiro.minibanco.transacao;

import com.lucascarreiro.minibanco.cliente.Cliente;
import com.lucascarreiro.minibanco.conta.Conta;
import com.lucascarreiro.minibanco.conta.ContaRepository;
import com.lucascarreiro.minibanco.shared.exception.RecursoNaoEncontradoException;
import com.lucascarreiro.minibanco.shared.exception.RegraNegocioException;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtratoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ExtratoService service;

    private final Pageable pageable = PageRequest.of(0, 20);

    @Test
    void deveLancarExcecaoQuandoContaNaoExiste() {
        when(contaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.consultar(99L, null, null, pageable))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");

        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void deveRejeitarDataInicialPosteriorAFinal() {
        when(contaRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.consultar(1L,
                LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 10), pageable))
                .isInstanceOf(RegraNegocioException.class);

        verifyNoInteractions(transacaoRepository);
    }

    @Test
    void deveUsarUltimos30DiasQuandoDatasNaoInformadas() {
        when(contaRepository.existsById(1L)).thenReturn(true);
        when(transacaoRepository.findByContaIdAndDataHoraBetween(eq(1L), any(), any(), eq(pageable)))
                .thenReturn(Page.empty());

        service.consultar(1L, null, null, pageable);

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
        Conta conta = new Conta("00000001", new Cliente("Maria Silva", "52998224725", "maria@email.com"));
        conta.depositar(new BigDecimal("150.00"));
        Transacao deposito = new Transacao(conta, TipoTransacao.DEPOSITO, new BigDecimal("150.00"), null);

        LocalDate dataInicio = LocalDate.of(2026, 10, 1);
        LocalDate dataFim = LocalDate.of(2026, 10, 15);

        when(contaRepository.existsById(1L)).thenReturn(true);
        when(transacaoRepository.findByContaIdAndDataHoraBetween(
                1L, dataInicio.atStartOfDay(), dataFim.atTime(LocalTime.MAX), pageable))
                .thenReturn(new PageImpl<>(List.of(deposito), pageable, 1));

        Page<ExtratoItemResponse> resultado = service.consultar(1L, dataInicio, dataFim, pageable);

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        ExtratoItemResponse item = resultado.getContent().get(0);
        assertThat(item.tipo()).isEqualTo(TipoTransacao.DEPOSITO);
        assertThat(item.valor()).isEqualByComparingTo("150.00");
        assertThat(item.saldoApos()).isEqualByComparingTo("150.00");
        assertThat(item.contaContraparte()).isNull();
    }
}