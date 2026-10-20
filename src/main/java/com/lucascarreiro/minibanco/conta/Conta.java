package com.lucascarreiro.minibanco.conta;

import com.lucascarreiro.minibanco.cliente.Cliente;
import com.lucascarreiro.minibanco.shared.exception.RegraNegocioException;
import com.lucascarreiro.minibanco.shared.exception.SaldoInsuficienteException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "contas")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String numero;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm;

    /** Controle de concorrência otimista: evita que duas operações simultâneas sobrescrevam o saldo. */
    @Version
    private Long version;

    protected Conta() {
        // exigido pelo JPA
    }

    public Conta(String numero, Cliente cliente) {
        this.numero = numero;
        this.cliente = cliente;
        this.criadaEm = LocalDateTime.now();
    }

    // A regra de negócio fica na própria entidade (modelo rico), não espalhada no service.
    public void depositar(BigDecimal valor) {
        validarValorPositivo(valor);
        this.saldo = this.saldo.add(valor);
    }

    public void sacar(BigDecimal valor) {
        validarValorPositivo(valor);
        if (this.saldo.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException();
        }
        this.saldo = this.saldo.subtract(valor);
    }

    private void validarValorPositivo(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor deve ser maior que zero");
        }
    }

    public boolean pertenceA(Long clienteId) {
        return Objects.equals(cliente.getId(), clienteId);
    }

    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public BigDecimal getSaldo() { return saldo; }
    public Cliente getCliente() { return cliente; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
}
