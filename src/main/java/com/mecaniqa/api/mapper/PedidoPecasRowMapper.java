package com.mecaniqa.api.mapper;

import com.mecaniqa.api.exception.ErroAcessoDadosException;
import com.mecaniqa.api.model.PedidoPecas;
import com.mecaniqa.api.model.StatusPedidoPecas;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** Converte uma linha do ResultSet (tabela pedido_pecas) em um objeto PedidoPecas, sem os itens. */
public final class PedidoPecasRowMapper {
    private PedidoPecasRowMapper() {}

    public static PedidoPecas map(ResultSet linha) {
        try {
            PedidoPecas pedido = new PedidoPecas();
            pedido.setCodigo(linha.getLong("codigo"));
            String status = linha.getString("status");
            if (status != null) pedido.setStatus(StatusPedidoPecas.valueOf(status));
            Timestamp dataCriacao = linha.getTimestamp("data_criacao");
            if (dataCriacao != null) pedido.setDataCriacao(dataCriacao.toLocalDateTime());
            Timestamp dataAtualizacao = linha.getTimestamp("data_ultima_atualizacao");
            if (dataAtualizacao != null) pedido.setDataUltimaAtualizacao(dataAtualizacao.toLocalDateTime());
            return pedido;
        } catch (SQLException causa) {
            throw new ErroAcessoDadosException("Não foi possível converter a linha retornada do banco em um Pedido de Peças.", causa);
        }
    }
}
