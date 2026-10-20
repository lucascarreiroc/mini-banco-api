package com.lucascarreiro.minibanco.integracao;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransferenciaIntegrationTest extends IntegracaoBase {

    @Test
    void deveTransferirEGravarHistoricoNasDuasContas() throws Exception {
        cadastrarMariaEJoao();
        String tokenMaria = login(CPF_MARIA, SENHA_MARIA);
        String tokenJoao = login(CPF_JOAO, SENHA_JOAO);
        Long contaMaria = abrirConta(tokenMaria);
        Long contaJoao = abrirConta(tokenJoao);
        depositar(tokenMaria, contaMaria, "500.00");

        mockMvc.perform(post("/transferencias")
                        .header(AUTHORIZATION, bearer(tokenMaria))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"contaOrigemId": %d, "contaDestinoId": %d, "valor": 200.00}
                                """.formatted(contaMaria, contaJoao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoAtualOrigem").value(300.0));

        mockMvc.perform(get("/contas/{id}", contaJoao).header(AUTHORIZATION, bearer(tokenJoao)))
                .andExpect(jsonPath("$.saldo").value(200.0));

        mockMvc.perform(get("/contas/{id}/extrato", contaMaria).header(AUTHORIZATION, bearer(tokenMaria)))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].tipo").value("TRANSFERENCIA_ENVIADA"))
                .andExpect(jsonPath("$.content[1].tipo").value("DEPOSITO"));

        mockMvc.perform(get("/contas/{id}/extrato", contaJoao).header(AUTHORIZATION, bearer(tokenJoao)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].tipo").value("TRANSFERENCIA_RECEBIDA"));
    }

    @Test
    void deveBloquearMovimentacaoAcimaDoLimiteDiario() throws Exception {
        cadastrarMariaEJoao();
        String tokenMaria = login(CPF_MARIA, SENHA_MARIA);
        String tokenJoao = login(CPF_JOAO, SENHA_JOAO);
        Long contaMaria = abrirConta(tokenMaria);
        Long contaJoao = abrirConta(tokenJoao);
        depositar(tokenMaria, contaMaria, "8000.00");

        mockMvc.perform(post("/transferencias")
                        .header(AUTHORIZATION, bearer(tokenMaria))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"contaOrigemId": %d, "contaDestinoId": %d, "valor": 3000.00}
                                """.formatted(contaMaria, contaJoao)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/contas/{id}/saque", contaMaria)
                        .header(AUTHORIZATION, bearer(tokenMaria))
                        .contentType(APPLICATION_JSON)
                        .content("{\"valor\": 2000.00}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/contas/{id}/saque", contaMaria)
                        .header(AUTHORIZATION, bearer(tokenMaria))
                        .contentType(APPLICATION_JSON)
                        .content("{\"valor\": 0.01}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value(containsString("R$ 0.00")));

        mockMvc.perform(get("/contas/{id}/limite", contaMaria).header(AUTHORIZATION, bearer(tokenMaria)))
                .andExpect(jsonPath("$.utilizadoHoje").value(5000.0))
                .andExpect(jsonPath("$.disponivelHoje").value(0.0));
    }
}