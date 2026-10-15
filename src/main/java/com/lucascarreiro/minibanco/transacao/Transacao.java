package com.lucascarreiro.minibanco.transacao;

import com.lucascarreiro.minibanco.conta.Conta;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_id")
    private Conta conta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoTransacao tipo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "saldo_apos", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoApos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_contraparte_id")
    private Conta contaContraparte;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    protected Transacao() {
        // exigido pelo JPA
    }

    public Transacao(Conta conta, TipoTransacao tipo, BigDecimal valor, Conta contaContraparte) {
        this.conta = conta;
        this.tipo = tipo;
        this.valor = valor;
        this.saldoApos = conta.getSaldo();
        this.contaContraparte = contaContraparte;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Conta getConta() { return conta; }
    public TipoTransacao getTipo() { return tipo; }
    public BigDecimal getValor() { return valor; }
    public BigDecimal getSaldoApos() { return saldoApos; }
    public Conta getContaContraparte() { return contaContraparte; }
    public LocalDateTime getDataHora() { return dataHora; }
}