package com.mecaniqa.api.mapper;

import com.mecaniqa.api.exception.ErroAcessoDadosException;
import com.mecaniqa.api.model.StatusOrdemServico;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** Lê os campos base (não relacionais) de uma linha da tabela ordem_servico. */
public final class OrdemServicoRowMapper {
    private OrdemServicoRowMapper() {}

    public record LinhaOrdemServico(Long codigo, String descricao, StatusOrdemServico status,
                                     java.time.LocalDateTime dataCriacao, java.time.LocalDateTime dataUltimaAtualizacao) {}

    public static LinhaOrdemServico map(ResultSet linha) {
        try {
            Timestamp dataCriacao = linha.getTimestamp("data_criacao");
            Timestamp dataAtualizacao = linha.getTimestamp("data_ultima_atualizacao");
            String status = linha.getString("status");
            return new LinhaOrdemServico(
                    linha.getLong("codigo"),
                    linha.getString("descricao"),
                    status == null ? null : StatusOrdemServico.valueOf(status),
                    dataCriacao == null ? null : dataCriacao.toLocalDateTime(),
                    dataAtualizacao == null ? null : dataAtualizacao.toLocalDateTime());
        } catch (SQLException causa) {
            throw new ErroAcessoDadosException("Não foi possível converter a linha retornada do banco em uma Ordem de Serviço.", causa);
        }
    }
}
