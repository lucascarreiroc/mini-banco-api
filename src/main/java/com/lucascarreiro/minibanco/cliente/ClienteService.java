package com.lucascarreiro.minibanco.cliente;

import com.lucascarreiro.minibanco.shared.exception.RecursoNaoEncontradoException;
import com.lucascarreiro.minibanco.shared.exception.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ClienteResponse cadastrar(ClienteRequest request) {
        String cpf = request.cpfSomenteNumeros();

        if (repository.existsByCpf(cpf)) {
            throw new RegraNegocioException("Já existe um cliente com este CPF");
        }
        if (repository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Já existe um cliente com este e-mail");
        }

        Cliente cliente = repository.save(new Cliente(request.nome(), cpf, request.email()));
        return ClienteResponse.de(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + id));
        return ClienteResponse.de(cliente);
    }
}
