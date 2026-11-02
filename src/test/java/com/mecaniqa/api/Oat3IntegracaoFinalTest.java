package com.mecaniqa.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecaniqa.api.db.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cenário de teste (24/10): executar as requisições (via Collection/API) e validar
 * diretamente no MySQL se a inserção e os relacionamentos foram cadastrados corretamente.
 */
@SpringBootTest
@AutoConfigureMockMvc
class Oat3IntegracaoFinalTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private JsonNode enviar(MockHttpServletRequestBuilder request, String body, int status) throws Exception {
        String resposta = mvc.perform(request.contentType("application/json").content(body))
                .andExpect(status().is(status)).andReturn().getResponse().getContentAsString();
        return resposta.isEmpty() ? json.nullNode() : json.readTree(resposta);
    }

    @Test void crudsIntegradosPersistemNoMySqlComRelacionamentosCorretos() throws Exception {
        long peca = enviar(post("/api/pecas"),
                "{\"codigoBarras\":\"OAT3-001\",\"quantidadeEstoque\":5,\"categoria\":\"MOTOR\"}", 201)
                .get("codigo").asLong();
        long servico = enviar(post("/api/servicos"), "{\"nome\":\"Alinhamento\",\"custoTabelado\":80}", 201)
                .get("codigo").asLong();
        long pedido = enviar(post("/api/pedidos-pecas"), "{}", 201).get("codigo").asLong();
        enviar(post("/api/pedidos-pecas/" + pedido + "/itens"),
                "{\"codigoPeca\":" + peca + ",\"quantidade\":2}", 200);
        long os = enviar(post("/api/ordens-servico"),
                "{\"descricao\":\"OAT3\",\"codigosServicos\":[" + servico + "],\"codigosPedidosPecas\":[" + pedido + "]}", 201)
                .get("codigo").asLong();

        try (Connection conexao = DatabaseConnection.obter()) {
            assertTrue(existe(conexao, "SELECT 1 FROM peca WHERE codigo = ?", peca));
            assertTrue(existe(conexao, "SELECT 1 FROM servico WHERE codigo = ?", servico));
            assertTrue(existe(conexao, "SELECT 1 FROM pedido_pecas WHERE codigo = ?", pedido));
            assertTrue(existe(conexao, "SELECT 1 FROM ordem_servico WHERE codigo = ?", os));
            assertTrue(existeRelacionamentoItem(conexao, pedido, peca, 2));
            assertTrue(existeRelacionamento(conexao,
                    "SELECT 1 FROM ordem_servico_servico WHERE ordem_servico_codigo = ? AND servico_codigo = ?", os, servico));
            assertTrue(existeRelacionamento(conexao,
                    "SELECT 1 FROM ordem_servico_pedido_pecas WHERE ordem_servico_codigo = ? AND pedido_pecas_codigo = ?", os, pedido));
        }
    }

    private boolean existe(Connection conexao, String sql, long codigo) throws Exception {
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setLong(1, codigo);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next();
            }
        }
    }

    private boolean existeRelacionamento(Connection conexao, String sql, long a, long b) throws Exception {
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setLong(1, a);
            comando.setLong(2, b);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next();
            }
        }
    }

    private boolean existeRelacionamentoItem(Connection conexao, long pedido, long peca, int quantidade) throws Exception {
        try (PreparedStatement comando = conexao.prepareStatement(
                "SELECT quantidade FROM item_pedido WHERE pedido_codigo = ? AND peca_codigo = ?")) {
            comando.setLong(1, pedido);
            comando.setLong(2, peca);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() && resultado.getInt("quantidade") == quantidade;
            }
        }
    }
}
