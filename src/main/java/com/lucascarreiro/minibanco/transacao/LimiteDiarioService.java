package com.lucascarreiro.minibanco.transacao;

import com.lucascarreiro.minibanco.conta.Conta;
import com.lucascarreiro.minibanco.shared.config.LimiteProperties;
import com.lucascarreiro.minibanco.shared.exception.LimiteDiarioExcedidoException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

@Service
public class LimiteDiarioService {

    private static final Set<TipoTransacao> TIPOS_QUE_CONSOMEM_LIMITE =
            EnumSet.of(TipoTransacao.SAQUE, TipoTransacao.TRANSFERENCIA_ENVIADA);

    private final TransacaoRepository transacaoRepository;
    private final LimiteProperties limites;
    private final Clock clock;

    public LimiteDiarioService(TransacaoRepository transacaoRepository, LimiteProperties limites, Clock clock) {
        this.transacaoRepository = transacaoRepository;
        this.limites = limites;
        this.clock = clock;
    }

    public void validar(Conta conta, BigDecimal valor) {
        BigDecimal disponivel = calcularDisponivel(conta);
        if (valor.compareTo(disponivel) > 0) {
            throw new LimiteDiarioExcedidoException(disponivel);
        }
    }

    public LimiteResponse consultar(Conta conta) {
        BigDecimal utilizado = calcularUtilizadoHoje(conta);
        return new LimiteResponse(limites.diario(), utilizado, disponivelAPartirDe(utilizado));
    }

    private BigDecimal calcularDisponivel(Conta conta) {
        return disponivelAPartirDe(calcularUtilizadoHoje(conta));
    }

    private BigDecimal disponivelAPartirDe(BigDecimal utilizado) {
        return limites.diario().subtract(utilizado).max(BigDecimal.ZERO);
    }

    private BigDecimal calcularUtilizadoHoje(Conta conta) {
        LocalDate hoje = LocalDate.now(clock);
        BigDecimal soma = transacaoRepository.somarValoresNoPeriodo(
                conta.getId(), TIPOS_QUE_CONSOMEM_LIMITE,
                hoje.atStartOfDay(), hoje.plusDays(1).atStartOfDay());
        return soma != null ? soma : BigDecimal.ZERO;
    }
}