package com.mecaniqa.api.repository;

import com.mecaniqa.api.mapper.OrdemServicoRowMapper;
import com.mecaniqa.api.mapper.OrdemServicoRowMapper.LinhaOrdemServico;
import com.mecaniqa.api.mapper.ServicoRowMapper;
import com.mecaniqa.api.model.OrdemServico;
import com.mecaniqa.api.model.PedidoPecas;
import com.mecaniqa.api.model.Servico;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class OrdemServicoRepository extends RepositorioJdbcBase<OrdemServico> {
    private static final OrdemServicoRepository INSTANCE = new OrdemServicoRepository();
    private final PedidoPecasRepository pedidosPecas = PedidoPecasRepository.getInstance();

    private OrdemServicoRepository() {}
    public static OrdemServicoRepository getInstance() { return INSTANCE; }

    public OrdemServico save(OrdemServico ordem) {
        if (ordem.getCodigo() == null) {
            long codigoGerado = executarInsercao(
                    "INSERT INTO ordem_servico (descricao, status, data_criacao, data_ultima_atualizacao) VALUES (?, ?, ?, ?)",
                    comando -> vincularCampos(comando, ordem));
            ordem.setCodigo(codigoGerado);
        } else {
            executarAtualizacao(
                    "UPDATE ordem_servico SET descricao=?, status=?, data_criacao=?, data_ultima_atualizacao=? WHERE codigo=?",
                    comando -> {
                        vincularCampos(comando, ordem);
                        comando.setLong(5, ordem.getCodigo());
                    });
        }
        salvarRelacionamentos(ordem);
        return ordem;
    }

    public Optional<OrdemServico> findById(Long codigo) {
        Optional<LinhaOrdemServico> linha = consultarUm("SELECT * FROM ordem_servico WHERE codigo = ?",
                comando -> comando.setLong(1, codigo), OrdemServicoRowMapper::map);
        if (linha.isEmpty()) return Optional.empty();

        LinhaOrdemServico dados = linha.get();
        List<Servico> servicos = consultar(
                "SELECT servico.* FROM ordem_servico_servico JOIN servico ON ordem_servico_servico.servico_codigo = servico.codigo "
                        + "WHERE ordem_servico_servico.ordem_servico_codigo = ?",
                comando -> comando.setLong(1, codigo), ServicoRowMapper::map);
        List<Long> codigosPedidos = consultar(
                "SELECT pedido_pecas_codigo FROM ordem_servico_pedido_pecas WHERE ordem_servico_codigo = ?",
                comando -> comando.setLong(1, codigo), resultado -> resultado.getLong("pedido_pecas_codigo"));

        OrdemServico.Builder builder = OrdemServico.builder().descricao(dados.descricao()).dataCriacao(dados.dataCriacao());
        servicos.forEach(builder::adicionarServico);
        for (Long codigoPedido : codigosPedidos) {
            PedidoPecas pedido = pedidosPecas.findById(codigoPedido).orElseThrow();
            builder.adicionarPedidoPecas(pedido);
        }
        OrdemServico ordem = builder.build();
        ordem.setCodigo(dados.codigo());
        if (dados.status() != null) ordem.setStatus(dados.status());
        if (dados.dataUltimaAtualizacao() != null) ordem.setDataUltimaAtualizacao(dados.dataUltimaAtualizacao());
        return Optional.of(ordem);
    }

    private void salvarRelacionamentos(OrdemServico ordem) {
        executarAtualizacao("DELETE FROM ordem_servico_servico WHERE ordem_servico_codigo = ?",
                comando -> comando.setLong(1, ordem.getCodigo()));
        for (Servico servico : ordem.getServicos()) {
            executarAtualizacao("INSERT INTO ordem_servico_servico (ordem_servico_codigo, servico_codigo) VALUES (?, ?)",
                    comando -> {
                        comando.setLong(1, ordem.getCodigo());
                        comando.setLong(2, servico.getCodigo());
                    });
        }

        executarAtualizacao("DELETE FROM ordem_servico_pedido_pecas WHERE ordem_servico_codigo = ?",
                comando -> comando.setLong(1, ordem.getCodigo()));
        for (PedidoPecas pedido : ordem.getPedidosPecas()) {
            executarAtualizacao("INSERT INTO ordem_servico_pedido_pecas (ordem_servico_codigo, pedido_pecas_codigo) VALUES (?, ?)",
                    comando -> {
                        comando.setLong(1, ordem.getCodigo());
                        comando.setLong(2, pedido.getCodigo());
                    });
        }
    }

    private void vincularCampos(PreparedStatement comando, OrdemServico ordem) throws SQLException {
        comando.setString(1, ordem.getDescricao());
        comando.setString(2, ordem.getStatus() == null ? null : ordem.getStatus().name());
        comando.setTimestamp(3, ordem.getDataCriacao() == null ? null : Timestamp.valueOf(ordem.getDataCriacao()));
        comando.setTimestamp(4, ordem.getDataUltimaAtualizacao() == null ? null : Timestamp.valueOf(ordem.getDataUltimaAtualizacao()));
    }
}
