package com.mecaniqa.api;

import com.mecaniqa.api.db.DatabaseConnection;
import com.mecaniqa.api.exception.ErroAcessoDadosException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Oat3PersistindoTest {
    @Autowired MockMvc mvc;

    // Cenário de teste (09/10): iniciar a conexão com o banco e lançar um erro claro caso as credenciais estejam erradas.
    @Test void lancaErroClaroQuandoCredenciaisDoBancoEstaoErradas() {
        ErroAcessoDadosException erro = assertThrows(ErroAcessoDadosException.class,
                () -> DatabaseConnection.obter("jdbc:mysql://localhost:3306/mecaniqa", "root", "senha-incorreta"));
        assertNotNull(erro.getMessage());
        assertTrue(erro.getMessage().toLowerCase().contains("banco de dados"));
    }

    // A aplicação consegue de fato iniciar a conexão com o banco de dados configurado.
    @Test void iniciaConexaoComBancoDeDadosComSucesso() throws Exception {
        try (var conexao = DatabaseConnection.obter()) {
            assertFalse(conexao.isClosed());
        }
    }

    // Cenário de teste (16/10): busca por ID inexistente retorna 404 de forma manual via exceção customizada.
    @Test void buscaPecaPorIdInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/pecas/99999999")).andExpect(status().isNotFound());
    }

    @Test void buscaServicoPorIdInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/servicos/99999999")).andExpect(status().isNotFound());
    }
}
