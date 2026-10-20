package com.lucascarreiro.minibanco.transferencia;

import com.lucascarreiro.minibanco.conta.Conta;
import com.lucascarreiro.minibanco.conta.ContaRepository;
import com.lucascarreiro.minibanco.shared.exception.AcessoNegadoException;
import com.lucascarreiro.minibanco.shared.exception.RecursoNaoEncontradoException;
import com.lucascarreiro.minibanco.shared.exception.RegraNegocioException;
import com.lucascarreiro.minibanco.transacao.LimiteDiarioService;
import com.lucascarreiro.minibanco.transacao.TipoTransacao;
import com.lucascarreiro.minibanco.transacao.Transacao;
import com.lucascarreiro.minibanco.transacao.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransferenciaService {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;
    private final LimiteDiarioService limiteDiarioService;

    public TransferenciaService(ContaRepository contaRepository, TransacaoRepository transacaoRepository, LimiteDiarioService limiteDiarioService) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
        this.limiteDiarioService = limiteDiarioService;
    }

    @Transactional
    public TransferenciaResponse transferir(TransferenciaRequest request, Long clienteId) {
        if (request.contaOrigemId().equals(request.contaDestinoId())) {
            throw new RegraNegocioException("Não é possível transferir para a mesma conta");
        }

        Conta origem = buscarConta(request.contaOrigemId());
        if (!origem.pertenceA(clienteId)) {
            throw new AcessoNegadoException();
        }
        Conta destino = buscarConta(request.contaDestinoId());

        limiteDiarioService.validar(origem, request.valor());

        origem.sacar(request.valor());
        destino.depositar(request.valor());

        transacaoRepository.save(new Transacao(origem, TipoTransacao.TRANSFERENCIA_ENVIADA, request.valor(), destino));
        transacaoRepository.save(new Transacao(destino, TipoTransacao.TRANSFERENCIA_RECEBIDA, request.valor(), origem));

        return new TransferenciaResponse(
                origem.getId(), destino.getId(), request.valor(),
                origem.getSaldo(), LocalDateTime.now());
    }

    private Conta buscarConta(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada: " + id));
    }
}