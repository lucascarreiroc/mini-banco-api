package com.lucascarreiro.minibanco.conta;

import com.lucascarreiro.minibanco.cliente.Cliente;
import com.lucascarreiro.minibanco.cliente.ClienteRepository;
import com.lucascarreiro.minibanco.shared.exception.AcessoNegadoException;
import com.lucascarreiro.minibanco.shared.exception.RecursoNaoEncontradoException;
import com.lucascarreiro.minibanco.transacao.TipoTransacao;
import com.lucascarreiro.minibanco.transacao.Transacao;
import com.lucascarreiro.minibanco.transacao.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
public class ContaService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;
    private final TransacaoRepository transacaoRepository;

    public ContaService(ContaRepository contaRepository, ClienteRepository clienteRepository, TransacaoRepository transacaoRepository) {
        this.contaRepository = contaRepository;
        this.clienteRepository = clienteRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional
    public ContaResponse abrir(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + clienteId));

        Conta conta = contaRepository.save(new Conta(gerarNumeroUnico(), cliente));
        return ContaResponse.de(conta);
    }

    @Transactional(readOnly = true)
    public List<ContaResponse> listarDoCliente(Long clienteId) {
        return contaRepository.findByClienteIdOrderByCriadaEmAsc(clienteId).stream()
                .map(ContaResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarPorId(Long id, Long clienteId) {
        return ContaResponse.de(buscarContaDoCliente(id, clienteId));
    }

    @Transactional
    public ContaResponse depositar(Long id, BigDecimal valor, Long clienteId) {
        Conta conta = buscarContaDoCliente(id, clienteId);
        conta.depositar(valor);
        transacaoRepository.save(new Transacao(conta, TipoTransacao.DEPOSITO, valor, null));
        return ContaResponse.de(conta);
    }

    @Transactional
    public ContaResponse sacar(Long id, BigDecimal valor, Long clienteId) {
        Conta conta = buscarContaDoCliente(id, clienteId);
        conta.sacar(valor);
        transacaoRepository.save(new Transacao(conta, TipoTransacao.SAQUE, valor, null));
        return ContaResponse.de(conta);
    }

    private Conta buscarContaDoCliente(Long id, Long clienteId) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada: " + id));
        if (!conta.pertenceA(clienteId)) {
            throw new AcessoNegadoException();
        }
        return conta;
    }

    private String gerarNumeroUnico() {
        String numero;
        do {
            numero = String.format("%08d", RANDOM.nextInt(100_000_000));
        } while (contaRepository.existsByNumero(numero));
        return numero;
    }
}
