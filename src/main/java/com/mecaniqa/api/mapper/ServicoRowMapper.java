package com.mecaniqa.api.mapper;

import com.mecaniqa.api.exception.ErroAcessoDadosException;
import com.mecaniqa.api.model.Servico;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** Converte uma linha do ResultSet (tabela servico) em um objeto Servico. */
public final class ServicoRowMapper {
    private ServicoRowMapper() {}

    public static Servico map(ResultSet linha) {
        try {
            Servico servico = new Servico();
            servico.setCodigo(linha.getLong("codigo"));
            servico.setNome(linha.getString("nome"));
            servico.setTempoEstimadoMinutos((Integer) linha.getObject("tempo_estimado_minutos"));
            BigDecimal custoTabelado = linha.getBigDecimal("custo_tabelado");
            servico.setCustoTabelado(custoTabelado == null ? null : custoTabelado.doubleValue());
            Timestamp dataCriacao = linha.getTimestamp("data_criacao");
            if (dataCriacao != null) servico.setDataCriacao(dataCriacao.toLocalDateTime());
            Timestamp dataAtualizacao = linha.getTimestamp("data_ultima_atualizacao");
            if (dataAtualizacao != null) servico.setDataUltimaAtualizacao(dataAtualizacao.toLocalDateTime());
            return servico;
        } catch (SQLException causa) {
            throw new ErroAcessoDadosException("Não foi possível converter a linha retornada do banco em um Serviço.", causa);
        }
    }
}
