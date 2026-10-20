package com.lucascarreiro.minibanco.integracao;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
public abstract class IntegracaoBase {

    protected static final String CPF_MARIA = "529.982.247-25";
    protected static final String SENHA_MARIA = "senhaMaria123";
    protected static final String CPF_JOAO = "111.444.777-35";
    protected static final String SENHA_JOAO = "senhaJoao123";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limparBanco() {
        jdbcTemplate.execute("TRUNCATE TABLE transacoes, contas, clientes RESTART IDENTITY CASCADE");
    }

    protected void cadastrarMariaEJoao() throws Exception {
        cadastrarCliente("Maria Silva", CPF_MARIA, "maria@email.com", SENHA_MARIA);
        cadastrarCliente("João Souza", CPF_JOAO, "joao@email.com", SENHA_JOAO);
    }

    protected void cadastrarCliente(String nome, String cpf, String email, String senha) throws Exception {
        String json = """
                {"nome": "%s", "cpf": "%s", "email": "%s", "senha": "%s"}
                """.formatted(nome, cpf, email, senha);

        mockMvc.perform(post("/clientes").contentType(APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
    }

    protected String login(String cpf, String senha) throws Exception {
        String json = """
                {"cpf": "%s", "senha": "%s"}
                """.formatted(cpf, senha);

        String resposta = mockMvc.perform(post("/auth/login").contentType(APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.token");
    }

    protected Long abrirConta(String token) throws Exception {
        String resposta = mockMvc.perform(post("/contas").header(AUTHORIZATION, bearer(token)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.parse(resposta).read("$.id", Long.class);
    }

    protected void depositar(String token, Long contaId, String valor) throws Exception {
        mockMvc.perform(post("/contas/{id}/deposito", contaId)
                        .header(AUTHORIZATION, bearer(token))
                        .contentType(APPLICATION_JSON)
                        .content("{\"valor\": " + valor + "}"))
                .andExpect(status().isOk());
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }
}