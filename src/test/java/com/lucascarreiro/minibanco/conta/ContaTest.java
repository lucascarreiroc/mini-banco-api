package com.lucascarreiro.minibanco.conta;

import com.lucascarreiro.minibanco.cliente.Cliente;
import com.lucascarreiro.minibanco.shared.exception.RegraNegocioException;
import com.lucascarreiro.minibanco.shared.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContaTest {

    private Conta conta;

    @BeforeEach
    void setUp() {
        Cliente cliente = new Cliente("Maria Silva", "12345678909", "maria@email.com");
        conta = new Conta("00000001", cliente);
    }

    @Test
    void deveIniciarComSaldoZero() {
        assertThat(conta.getSaldo()).isEqualByComparingTo("0");
    }

    @Test
    void deveDepositarValorPositivo() {
        conta.depositar(new BigDecimal("150.75"));
        assertThat(conta.getSaldo()).isEqualByComparingTo("150.75");
    }

    @Test
    void deveSacarQuandoHaSaldo() {
        conta.depositar(new BigDecimal("100.00"));
        conta.sacar(new BigDecimal("40.00"));
        assertThat(conta.getSaldo()).isEqualByComparingTo("60.00");
    }

    @Test
    void naoDeveSacarAlemDoSaldo() {
        conta.depositar(new BigDecimal("50.00"));
        assertThatThrownBy(() -> conta.sacar(new BigDecimal("50.01")))
                .isInstanceOf(SaldoInsuficienteException.class);
        assertThat(conta.getSaldo()).isEqualByComparingTo("50.00");
    }

    @Test
    void naoDeveAceitarValorZeroOuNegativo() {
        assertThatThrownBy(() -> conta.depositar(BigDecimal.ZERO))
                .isInstanceOf(RegraNegocioException.class);
        assertThatThrownBy(() -> conta.sacar(new BigDecimal("-10")))
                .isInstanceOf(RegraNegocioException.class);
    }
}
