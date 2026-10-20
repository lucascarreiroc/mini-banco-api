package com.lucascarreiro.minibanco.integracao;

import com.lucascarreiro.minibanco.transacao.TransacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class TransferenciaRollbackIntegrationTest extends IntegracaoBase {

    @MockitoBean
    private TransacaoRepository transacaoRepository;

    @Test
    void deveDesfazerTudoQuandoFalharNoMeioDaTransferencia() throws Exception {
        cadastrarMariaEJoao();
        String tokenMaria = login(CPF_MARIA, SENHA_MARIA);
        String tokenJoao = login(CPF_JOAO, SENHA_JOAO);
        Long contaMaria = abrirConta(tokenMaria);
        Long contaJoao = abrirConta(tokenJoao);
        depositar(tokenMaria, contaMaria, "500.00");

        doThrow(new RuntimeException("Falha simulada ao gravar transação"))
                .when(transacaoRepository).save(any());

        assertThatThrownBy(() -> mockMvc.perform(post("/transferencias")
                .header(AUTHORIZATION, bearer(tokenMaria))
                .contentType(APPLICATION_JSON)
                .content("""
                        {"contaOrigemId": %d, "contaDestinoId": %d, "valor": 200.00}
                        """.formatted(contaMaria, contaJoao))))
                .hasRootCauseMessage("Falha simulada ao gravar transação");

        mockMvc.perform(get("/contas/{id}", contaMaria).header(AUTHORIZATION, bearer(tokenMaria)))
                .andExpect(jsonPath("$.saldo").value(500.0));

        mockMvc.perform(get("/contas/{id}", contaJoao).header(AUTHORIZATION, bearer(tokenJoao)))
                .andExpect(jsonPath("$.saldo").value(0.0));
    }
}