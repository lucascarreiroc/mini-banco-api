package com.lucascarreiro.minibanco.cliente;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    protected Cliente() {
        // exigido pelo JPA
    }

    public Cliente(String nome, String cpf, String email) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
