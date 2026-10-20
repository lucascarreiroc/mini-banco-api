package com.lucascarreiro.minibanco.conta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    boolean existsByNumero(String numero);
    List<Conta> findByClienteIdOrderByCriadaEmAsc(Long clienteId);
}
