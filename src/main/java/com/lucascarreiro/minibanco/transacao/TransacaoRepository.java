package com.lucascarreiro.minibanco.transacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @EntityGraph(attributePaths = "contaContraparte")
    Page<Transacao> findByContaIdAndDataHoraBetween(
            Long contaId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
}