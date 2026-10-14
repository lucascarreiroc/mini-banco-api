package com.lucascarreiro.minibanco.conta;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    boolean existsByNumero(String numero);
}
