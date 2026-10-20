package com.lucascarreiro.minibanco.transacao;

import com.lucascarreiro.minibanco.cliente.Cliente;
import com.lucascarreiro.minibanco.conta.Conta;
import com.lucascarreiro.minibanco.shared.config.LimiteProperties;
import com.lucascarreiro.minibanco.shared.exception.LimiteDiarioExcedidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LimiteDiarioServiceTest {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 20, 15, 30);

    @Mock
    private TransacaoRepository transacaoRepository;

    private LimiteDiarioService service;
    private Conta conta;

    @BeforeEach
    void setUp() {
        Clock relogioFixo = Clock.fixed(AGORA.atZone(FUSO).toInstant(), FUSO);
        service = new LimiteDiarioService(
                transacaoRepository, new LimiteProperties(new BigDecimal("5000.00")), relogioFixo);

        conta = new Conta("00000001", new Cliente("Maria Silva", "52998224725", "maria@email.com", "hash-qualquer"));
        ReflectionTestUtils.setField(conta, "id", 1L);
    }

    private void quandoUtilizadoHojeFor(BigDecimal valor) {
        when(transacaoRepository.somarValoresNoPeriodo(
                1L,
                EnumSet.of(TipoTransacao.SAQUE, TipoTransacao.TRANSFERENCIA_ENVIADA),
                LocalDateTime.of(2026, 10, 20, 0, 0),
                LocalDateTime.of(2026, 10, 21, 0, 0)))
                .thenReturn(valor);
    }

    @Test
    void devePermitirMovimentarExatamenteOQueResta() {
        quandoUtilizadoHojeFor(new BigDecimal("3000.00"));

        assertThatCode(() -> service.validar(conta, new BigDecimal("2000.00")))
                .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarValorAcimaDoDisponivel() {
        quandoUtilizadoHojeFor(new BigDecimal("3000.00"));

        assertThatThrownBy(() -> service.validar(conta, new BigDecimal("2000.01")))
                .isInstanceOf(LimiteDiarioExcedidoException.class)
                .hasMessageContaining("2000.00");
    }

    @Test
    void deveConsiderarLimiteCheioQuandoNaoHouveMovimentacao() {
        quandoUtilizadoHojeFor(null);

        LimiteResponse limite = service.consultar(conta);

        assertThat(limite.utilizadoHoje()).isEqualByComparingTo("0");
        assertThat(limite.disponivelHoje()).isEqualByComparingTo("5000.00");
    }

    @Test
    void naoDeveRetornarDisponivelNegativo() {
        quandoUtilizadoHojeFor(new BigDecimal("6000.00"));

        LimiteResponse limite = service.consultar(conta);

        assertThat(limite.disponivelHoje()).isEqualByComparingTo("0");
    }
}