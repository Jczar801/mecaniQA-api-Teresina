package com.mecaniqa.api.repository;

import com.mecaniqa.api.mapper.ServicoRowMapper;
import com.mecaniqa.api.model.Servico;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

public class ServicoRepository extends RepositorioJdbcBase<Servico> {
    private static final ServicoRepository INSTANCE = new ServicoRepository();

    private ServicoRepository() {}
    public static ServicoRepository getInstance() { return INSTANCE; }

    public Servico save(Servico servico) {
        if (servico.getCodigo() == null) {
            long codigoGerado = executarInsercao(
                    "INSERT INTO servico (nome, tempo_estimado_minutos, custo_tabelado, data_criacao, data_ultima_atualizacao) "
                            + "VALUES (?, ?, ?, ?, ?)",
                    comando -> vincularCampos(comando, servico));
            servico.setCodigo(codigoGerado);
        } else {
            executarAtualizacao(
                    "UPDATE servico SET nome=?, tempo_estimado_minutos=?, custo_tabelado=?, data_criacao=?, "
                            + "data_ultima_atualizacao=? WHERE codigo=?",
                    comando -> {
                        vincularCampos(comando, servico);
                        comando.setLong(6, servico.getCodigo());
                    });
        }
        return servico;
    }

    public List<Servico> findAll() {
        return consultar("SELECT * FROM servico ORDER BY codigo", comando -> {}, ServicoRowMapper::map);
    }

    public Optional<Servico> findById(Long codigo) {
        return consultarUm("SELECT * FROM servico WHERE codigo = ?", comando -> comando.setLong(1, codigo), ServicoRowMapper::map);
    }

    public boolean deleteById(Long codigo) {
        return executarAtualizacao("DELETE FROM servico WHERE codigo = ?", comando -> comando.setLong(1, codigo)) > 0;
    }

    private void vincularCampos(PreparedStatement comando, Servico servico) throws SQLException {
        comando.setString(1, servico.getNome());
        setNullableInt(comando, 2, servico.getTempoEstimadoMinutos());
        setNullableDouble(comando, 3, servico.getCustoTabelado());
        comando.setTimestamp(4, servico.getDataCriacao() == null ? null : Timestamp.valueOf(servico.getDataCriacao()));
        comando.setTimestamp(5, servico.getDataUltimaAtualizacao() == null ? null : Timestamp.valueOf(servico.getDataUltimaAtualizacao()));
    }

    private void setNullableInt(PreparedStatement comando, int indice, Integer valor) throws SQLException {
        if (valor == null) comando.setNull(indice, Types.INTEGER); else comando.setInt(indice, valor);
    }

    private void setNullableDouble(PreparedStatement comando, int indice, Double valor) throws SQLException {
        if (valor == null) comando.setNull(indice, Types.DOUBLE); else comando.setDouble(indice, valor);
    }
}
