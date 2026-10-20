package com.lucascarreiro.minibanco.transacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @EntityGraph(attributePaths = "contaContraparte")
    Page<Transacao> findByContaIdAndDataHoraBetween(
            Long contaId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    @Query("""
        SELECT SUM(t.valor) FROM Transacao t
        WHERE t.conta.id = :contaId
          AND t.tipo IN :tipos
          AND t.dataHora >= :inicio
          AND t.dataHora < :fim
        """)
    BigDecimal somarValoresNoPeriodo(@Param("contaId") Long contaId,
                                     @Param("tipos") Collection<TipoTransacao> tipos,
                                     @Param("inicio") LocalDateTime inicio,
                                     @Param("fim") LocalDateTime fim);
}