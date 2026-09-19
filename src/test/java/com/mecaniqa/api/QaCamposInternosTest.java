package com.mecaniqa.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class QaCamposInternosTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private JsonNode enviar(MockHttpServletRequestBuilder request, String body, int status) throws Exception {
        String resposta = mvc.perform(request.contentType("application/json").content(body))
                .andExpect(status().is(status)).andReturn().getResponse().getContentAsString();
        return resposta.isEmpty() ? json.nullNode() : json.readTree(resposta);
    }

    @Test void criaPecaSemCodigoNemDatasEGeraOsInternamente() throws Exception {
        JsonNode peca = enviar(post("/api/pecas"), "{\"categoria\":\"MOTOR\"}", 201);
        assertNotNull(peca.get("codigo"));
        assertTrue(peca.get("codigo").asLong() > 0);
        assertNotNull(peca.get("dataCadastro"));
        assertNotNull(peca.get("dataUltimaAtualizacao"));
        assertTrue(peca.get("codigoBarras").isNull());
        assertTrue(peca.get("fornecedorMarca").isNull());
    }

    @Test void criaServicoSemCodigoNemDatasEGeraOsInternamente() throws Exception {
        JsonNode servico = enviar(post("/api/servicos"), "{\"nome\":\"Revisão\"}", 201);
        assertNotNull(servico.get("codigo"));
        assertTrue(servico.get("codigo").asLong() > 0);
        assertNotNull(servico.get("dataCriacao"));
        assertNotNull(servico.get("dataUltimaAtualizacao"));
        assertTrue(servico.get("tempoEstimadoMinutos").isNull());
    }

    @Test void criaPedidoDePecasComCorpoVazioSemAtributosInternos() throws Exception {
        JsonNode pedido = enviar(post("/api/pedidos-pecas"), "{}", 201);
        assertNotNull(pedido.get("codigo"));
        assertEquals("ORCANDO", pedido.get("status").asText());
        assertNotNull(pedido.get("dataCriacao"));
        assertTrue(pedido.get("itens").isArray());
        assertEquals(0, pedido.get("itens").size());
    }

    @Test void criaOrdemServicoComCorpoVazioSemAtributosInternos() throws Exception {
        JsonNode os = enviar(post("/api/ordens-servico"), "{}", 201);
        assertNotNull(os.get("codigo"));
        assertEquals("ABERTO", os.get("status").asText());
        assertNotNull(os.get("dataCriacao"));
        assertTrue(os.get("servicos").isArray());
        assertEquals(0, os.get("servicos").size());
        assertTrue(os.get("pedidosPecas").isArray());
        assertEquals(0, os.get("pedidosPecas").size());
    }

    @Test void alteraStatusEnviandoApenasOAtributoStatus() throws Exception {
        long pedido = enviar(post("/api/pedidos-pecas"), "{}", 201).get("codigo").asLong();
        JsonNode atualizado = enviar(patch("/api/pedidos-pecas/" + pedido + "/status"), "{\"status\":\"PAGO_FATURADO\"}", 200);
        assertEquals("PAGO_FATURADO", atualizado.get("status").asText());

        long os = enviar(post("/api/ordens-servico"), "{}", 201).get("codigo").asLong();
        JsonNode osAtualizada = enviar(patch("/api/ordens-servico/" + os + "/status"), "{\"status\":\"EM_EXECUCAO\"}", 200);
        assertEquals("EM_EXECUCAO", osAtualizada.get("status").asText());
    }

    @Test void adicionaItemEnviandoApenasOsCamposObrigatorios() throws Exception {
        long peca = enviar(post("/api/pecas"), "{\"categoria\":\"FREIOS\"}", 201).get("codigo").asLong();
        long pedido = enviar(post("/api/pedidos-pecas"), "{}", 201).get("codigo").asLong();
        JsonNode atualizado = enviar(post("/api/pedidos-pecas/" + pedido + "/itens"),
                "{\"codigoPeca\":" + peca + ",\"quantidade\":1}", 200);
        assertEquals(1, atualizado.get("itens").size());
        assertFalse(atualizado.get("itens").get(0).has("pedido"));
    }

    @Test void ignoraAtributosInternosMesmoQuandoEnviadosPeloCliente() throws Exception {
        JsonNode peca = enviar(post("/api/pecas"),
                "{\"codigo\":99999999,\"categoria\":\"ACESSORIOS\",\"dataCadastro\":\"2000-01-01T00:00:00\",\"dataUltimaAtualizacao\":\"2000-01-01T00:00:00\"}",
                201);
        assertNotEquals(99999999, peca.get("codigo").asLong());
        assertFalse(peca.get("dataCadastro").asText().startsWith("2000"));
    }
}
