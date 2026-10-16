package com.lucascarreiro.minibanco.transacao;

import com.lucascarreiro.minibanco.conta.ContaRepository;
import com.lucascarreiro.minibanco.shared.exception.RecursoNaoEncontradoException;
import com.lucascarreiro.minibanco.shared.exception.RegraNegocioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class ExtratoService {

    private static final int PERIODO_PADRAO_DIAS = 30;

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;

    public ExtratoService(TransacaoRepository transacaoRepository, ContaRepository contaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.contaRepository = contaRepository;
    }

    @Transactional(readOnly = true)
    public Page<ExtratoItemResponse> consultar(Long contaId, LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        if (!contaRepository.existsById(contaId)) {
            throw new RecursoNaoEncontradoException("Conta não encontrada: " + contaId);
        }

        LocalDate fim = dataFim != null ? dataFim : LocalDate.now();
        LocalDate inicio = dataInicio != null ? dataInicio : fim.minusDays(PERIODO_PADRAO_DIAS);

        if (inicio.isAfter(fim)) {
            throw new RegraNegocioException("A data inicial não pode ser posterior à data final");
        }

        return transacaoRepository
                .findByContaIdAndDataHoraBetween(contaId, inicio.atStartOfDay(), fim.atTime(LocalTime.MAX), pageable)
                .map(ExtratoItemResponse::de);
    }
}