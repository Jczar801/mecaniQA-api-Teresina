package com.mecaniqa.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecaniqa.api.model.*;
import com.mecaniqa.api.repository.*;
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
class Oat2IntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private JsonNode enviar(MockHttpServletRequestBuilder request, String body, int status) throws Exception {
        String resposta = mvc.perform(request.contentType("application/json").content(body))
                .andExpect(status().is(status)).andReturn().getResponse().getContentAsString();
        return resposta.isEmpty() ? json.nullNode() : json.readTree(resposta);
    }

    private long peca() throws Exception {
        return enviar(post("/api/pecas"), "{\"categoria\":\"MOTOR\",\"quantidadeEstoque\":10}", 201).get("codigo").asLong();
    }

    @Test void executaAsCincoUserStoriesComMultiplasPecasEServicos() throws Exception {
        long primeira = peca(), segunda = peca();
        long servico = enviar(post("/api/servicos"), "{\"nome\":\"Revisão\",\"custoTabelado\":100}", 201).get("codigo").asLong();
        JsonNode pedido = enviar(post("/api/pedidos-pecas"), "{}", 201);
        assertEquals("ORCANDO", pedido.get("status").asText());
        long id = pedido.get("codigo").asLong();
        enviar(post("/api/pedidos-pecas/" + id + "/itens"), "{\"codigoPeca\":" + primeira + ",\"quantidade\":2}", 200);
        pedido = enviar(post("/api/pedidos-pecas/" + id + "/itens"), "{\"codigoPeca\":" + segunda + ",\"quantidade\":3}", 200);
        assertEquals(2, pedido.get("itens").size());
        pedido = enviar(post("/api/pedidos-pecas/" + id + "/itens"), "{\"codigoPeca\":" + primeira + ",\"quantidade\":4}", 200);
        assertEquals(6, pedido.get("itens").get(0).get("quantidade").asInt());
        PedidoPecas model = PedidoPecasRepository.getInstance().findById(id).orElseThrow();
        assertSame(model, model.getItens().get(0).getPedido());
        assertFalse(pedido.get("itens").get(0).has("pedido"));
        assertEquals(10, PecaRepository.getInstance().findById(primeira).orElseThrow().getQuantidadeEstoque());
        for (StatusPedidoPecas status : StatusPedidoPecas.values()) {
            assertEquals(status.name(), enviar(patch("/api/pedidos-pecas/" + id + "/status"),
                    "{\"status\":\"" + status + "\"}", 200).get("status").asText());
        }
        JsonNode os = enviar(post("/api/ordens-servico"),
                "{\"descricao\":\"Revisão\",\"codigosServicos\":[" + servico + "],\"codigosPedidosPecas\":[" + id + "]}", 201);
        assertEquals("ABERTO", os.get("status").asText());
        assertEquals(servico, os.get("servicos").get(0).get("codigo").asLong());
        assertEquals(2, os.get("pedidosPecas").get(0).get("itens").size());
        long codigoOS = os.get("codigo").asLong();
        String criacao = os.get("dataCriacao").asText();
        for (StatusOrdemServico status : StatusOrdemServico.values()) {
            os = enviar(patch("/api/ordens-servico/" + codigoOS + "/status"), "{\"status\":\"" + status + "\"}", 200);
            assertEquals(status.name(), os.get("status").asText());
            assertEquals(criacao, os.get("dataCriacao").asText());
        }
    }

    @Test void rejeitaEntradasInvalidasSemAlterarPedido() throws Exception {
        long peca = peca();
        long pedido = enviar(post("/api/pedidos-pecas"), "{}", 201).get("codigo").asLong();
        String rota = "/api/pedidos-pecas/" + pedido + "/itens";
        for (String body : new String[]{"{}", "{\"codigoPeca\":null,\"quantidade\":1}",
                "{\"codigoPeca\":" + peca + ",\"quantidade\":0}", "{\"codigoPeca\":" + peca + ",\"quantidade\":-1}"}) {
            enviar(post(rota), body, 400);
        }
        enviar(post(rota), "{\"codigoPeca\":99999999,\"quantidade\":1}", 404);
        assertTrue(PedidoPecasRepository.getInstance().findById(pedido).orElseThrow().getItens().isEmpty());
        enviar(post(rota), "{\"codigoPeca\":" + peca + ",\"quantidade\":2147483647}", 200);
        enviar(post(rota), "{\"codigoPeca\":" + peca + ",\"quantidade\":1}", 400);
        assertEquals(Integer.MAX_VALUE, PedidoPecasRepository.getInstance().findById(pedido).orElseThrow().getItens().get(0).getQuantidade());
        for (String body : new String[]{"{}", "{\"status\":null}", "{\"status\":\"INVALIDO\"}"}) {
            enviar(patch("/api/pedidos-pecas/" + pedido + "/status"), body, 400);
            enviar(patch("/api/ordens-servico/99999999/status"), body, 400);
        }
        enviar(post("/api/pedidos-pecas/99999999/itens"), "{\"codigoPeca\":" + peca + ",\"quantidade\":1}", 404);
        enviar(patch("/api/pedidos-pecas/99999999/status"), "{\"status\":\"ENTREGUE\"}", 404);
        enviar(patch("/api/ordens-servico/99999999/status"), "{\"status\":\"EXECUTADO\"}", 404);
        enviar(post("/api/ordens-servico"), "{\"codigosServicos\":[99999999]}", 404);
        enviar(post("/api/ordens-servico"), "{\"codigosPedidosPecas\":[99999999]}", 404);
        enviar(post("/api/ordens-servico"), "{\"codigosServicos\":[null]}", 400);
        enviar(post("/api/pedidos-pecas"), "{\"itens\":[null]}", 400);
        enviar(post("/api/pedidos-pecas"), "{", 400);
        enviar(post("/api/ordens-servico"), "null", 400);
    }

    @Test void aceitaItensIniciaisEIgnoraMetadadosEnviadosPeloCliente() throws Exception {
        long peca = peca();
        JsonNode pedido = enviar(post("/api/pedidos-pecas"), "{\"codigo\":99999999,\"status\":\"ENTREGUE\",\"itens\":[{\"codigoPeca\":" + peca + ",\"quantidade\":2}]}", 201);
        assertNotEquals(99999999, pedido.get("codigo").asLong());
        assertEquals("ORCANDO", pedido.get("status").asText());
        assertEquals(2, pedido.get("itens").get(0).get("quantidade").asInt());
        JsonNode os = enviar(post("/api/ordens-servico"), "{\"codigo\":99999999,\"status\":\"EXECUTADO\"}", 201);
        assertNotEquals(99999999, os.get("codigo").asLong());
        assertEquals("ABERTO", os.get("status").asText());
        assertTrue(os.get("servicos").isEmpty());
    }

    @Test void preservaCrudAnteriorComDTOsEProtegeDatasECodigos() throws Exception {
        for (String recurso : new String[]{"pecas", "servicos"}) {
            String base = "/api/" + recurso;
            String data = recurso.equals("pecas") ? "dataCadastro" : "dataCriacao";
            String body = "{\"codigo\":99999999,\"categoria\":\"MOTOR\",\"nome\":\"Teste\",\"" + data + "\":\"2000-01-01T00:00:00\"}";
            JsonNode criado = enviar(post(base), body, 201);
            long id = criado.get("codigo").asLong();
            assertNotEquals(99999999, id);
            assertFalse(criado.get(data).asText().startsWith("2000"));
            mvc.perform(get(base)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
            mvc.perform(get(base + "/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.codigo").value(id));
            JsonNode atualizado = enviar(put(base + "/" + id), body, 200);
            assertEquals(id, atualizado.get("codigo").asLong());
            assertEquals(criado.get(data), atualizado.get(data));
            mvc.perform(delete(base + "/" + id)).andExpect(status().isNoContent());
            mvc.perform(get(base + "/" + id)).andExpect(status().isNotFound());
            enviar(put(base + "/" + id), body, 404);
            mvc.perform(delete(base + "/" + id)).andExpect(status().isNotFound());
        }
    }

    @Test void builderCriaInstanciasIndependentesERepositoriosSaoSingletons() {
        OrdemServico.Builder builder = OrdemServico.builder().descricao("Revisão");
        OrdemServico primeira = builder.build();
        builder.adicionarServico(new Servico());
        OrdemServico segunda = builder.build();
        assertTrue(primeira.getServicos().isEmpty());
        assertEquals(1, segunda.getServicos().size());
        assertThrows(UnsupportedOperationException.class, () -> primeira.getServicos().add(new Servico()));
        assertSame(OrdemServicoRepository.getInstance(), OrdemServicoRepository.getInstance());
        assertSame(PedidoPecasRepository.getInstance(), PedidoPecasRepository.getInstance());
    }
}
