package com.lucascarreiro.minibanco.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
