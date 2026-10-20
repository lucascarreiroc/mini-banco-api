package com.lucascarreiro.minibanco.integracao;

import org.junit.jupiter.api.Test;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SegurancaIntegrationTest extends IntegracaoBase {

    @Test
    void deveNegarAcessoSemToken() throws Exception {
        mockMvc.perform(get("/contas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveNegarTokenComAssinaturaAdulterada() throws Exception {
        cadastrarMariaEJoao();
        String token = login(CPF_MARIA, SENHA_MARIA);

        String[] partes = token.split("\\.");
        String tokenAdulterado = partes[0] + "." + partes[1] + ".assinaturaFalsa";

        mockMvc.perform(get("/contas").header(AUTHORIZATION, bearer(tokenAdulterado)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRejeitarLoginComSenhaIncorreta() throws Exception {
        cadastrarMariaEJoao();

        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("{\"cpf\": \"" + CPF_MARIA + "\", \"senha\": \"senhaErrada\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("CPF ou senha inválidos"));
    }

    @Test
    void naoDevePermitirMexerNaContaDeOutroCliente() throws Exception {
        cadastrarMariaEJoao();
        String tokenMaria = login(CPF_MARIA, SENHA_MARIA);
        String tokenJoao = login(CPF_JOAO, SENHA_JOAO);
        Long contaJoao = abrirConta(tokenJoao);
        depositar(tokenJoao, contaJoao, "100.00");

        mockMvc.perform(post("/contas/{id}/saque", contaJoao)
                        .header(AUTHORIZATION, bearer(tokenMaria))
                        .contentType(APPLICATION_JSON)
                        .content("{\"valor\": 50.00}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/contas/{id}/extrato", contaJoao).header(AUTHORIZATION, bearer(tokenMaria)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/contas/{id}", contaJoao).header(AUTHORIZATION, bearer(tokenJoao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(100.0));
    }
}