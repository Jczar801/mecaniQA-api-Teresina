package com.mecaniqa.api.repository;

import com.mecaniqa.api.mapper.PecaRowMapper;
import com.mecaniqa.api.mapper.PedidoPecasRowMapper;
import com.mecaniqa.api.model.ItemPedido;
import com.mecaniqa.api.model.PedidoPecas;
import com.mecaniqa.api.model.Peca;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoPecasRepository extends RepositorioJdbcBase<PedidoPecas> {
    private static final PedidoPecasRepository INSTANCE = new PedidoPecasRepository();

    private PedidoPecasRepository() {}
    public static PedidoPecasRepository getInstance() { return INSTANCE; }

    public PedidoPecas save(PedidoPecas pedido) {
        if (pedido.getCodigo() == null) {
            long codigoGerado = executarInsercao(
                    "INSERT INTO pedido_pecas (status, data_criacao, data_ultima_atualizacao) VALUES (?, ?, ?)",
                    comando -> vincularCampos(comando, pedido));
            pedido.setCodigo(codigoGerado);
        } else {
            executarAtualizacao(
                    "UPDATE pedido_pecas SET status=?, data_criacao=?, data_ultima_atualizacao=? WHERE codigo=?",
                    comando -> {
                        vincularCampos(comando, pedido);
                        comando.setLong(4, pedido.getCodigo());
                    });
        }
        salvarItens(pedido);
        return pedido;
    }

    public Optional<PedidoPecas> findById(Long codigo) {
        Optional<PedidoPecas> encontrado = consultarUm("SELECT * FROM pedido_pecas WHERE codigo = ?",
                comando -> comando.setLong(1, codigo), PedidoPecasRowMapper::map);
        encontrado.ifPresent(pedido -> pedido.setItens(buscarItens(pedido)));
        return encontrado;
    }

    private List<ItemPedido> buscarItens(PedidoPecas pedido) {
        return consultar(
                "SELECT item_pedido.quantidade AS item_quantidade, peca.* "
                        + "FROM item_pedido JOIN peca ON item_pedido.peca_codigo = peca.codigo "
                        + "WHERE item_pedido.pedido_codigo = ?",
                comando -> comando.setLong(1, pedido.getCodigo()),
                linha -> {
                    Peca peca = PecaRowMapper.map(linha);
                    ItemPedido item = new ItemPedido();
                    item.setPedido(pedido);
                    item.setPeca(peca);
                    item.setQuantidade(linha.getInt("item_quantidade"));
                    return item;
                });
    }

    private void salvarItens(PedidoPecas pedido) {
        executarAtualizacao("DELETE FROM item_pedido WHERE pedido_codigo = ?", comando -> comando.setLong(1, pedido.getCodigo()));
        List<ItemPedido> itens = pedido.getItens() == null ? new ArrayList<>() : pedido.getItens();
        for (ItemPedido item : itens) {
            executarAtualizacao("INSERT INTO item_pedido (pedido_codigo, peca_codigo, quantidade) VALUES (?, ?, ?)",
                    comando -> {
                        comando.setLong(1, pedido.getCodigo());
                        comando.setLong(2, item.getPeca().getCodigo());
                        comando.setInt(3, item.getQuantidade());
                    });
        }
    }

    private void vincularCampos(PreparedStatement comando, PedidoPecas pedido) throws SQLException {
        comando.setString(1, pedido.getStatus() == null ? null : pedido.getStatus().name());
        comando.setTimestamp(2, pedido.getDataCriacao() == null ? null : Timestamp.valueOf(pedido.getDataCriacao()));
        comando.setTimestamp(3, pedido.getDataUltimaAtualizacao() == null ? null : Timestamp.valueOf(pedido.getDataUltimaAtualizacao()));
    }
}
