package com.mecaniqa.api.repository;

import com.mecaniqa.api.mapper.PecaRowMapper;
import com.mecaniqa.api.model.Peca;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

public class PecaRepository extends RepositorioJdbcBase<Peca> {
    private static final PecaRepository INSTANCE = new PecaRepository();

    private PecaRepository() {}
    public static PecaRepository getInstance() { return INSTANCE; }

    public Peca save(Peca peca) {
        if (peca.getCodigo() == null) {
            long codigoGerado = executarInsercao(
                    "INSERT INTO peca (codigo_barras, fornecedor_marca, quantidade_estoque, preco_custo, "
                            + "preco_venda, data_cadastro, data_ultima_atualizacao, tamanho, cor, categoria) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    comando -> vincularCampos(comando, peca));
            peca.setCodigo(codigoGerado);
        } else {
            executarAtualizacao(
                    "UPDATE peca SET codigo_barras=?, fornecedor_marca=?, quantidade_estoque=?, preco_custo=?, "
                            + "preco_venda=?, data_cadastro=?, data_ultima_atualizacao=?, tamanho=?, cor=?, categoria=? "
                            + "WHERE codigo=?",
                    comando -> {
                        vincularCampos(comando, peca);
                        comando.setLong(11, peca.getCodigo());
                    });
        }
        return peca;
    }

    public List<Peca> findAll() {
        return consultar("SELECT * FROM peca ORDER BY codigo", comando -> {}, PecaRowMapper::map);
    }

    public Optional<Peca> findById(Long codigo) {
        return consultarUm("SELECT * FROM peca WHERE codigo = ?", comando -> comando.setLong(1, codigo), PecaRowMapper::map);
    }

    public boolean deleteById(Long codigo) {
        return executarAtualizacao("DELETE FROM peca WHERE codigo = ?", comando -> comando.setLong(1, codigo)) > 0;
    }

    /** Consulta manual simples via JDBC puro, direto na tabela peca do MySQL. */
    public Optional<Peca> buscarPorCodigoBarrasNoBanco(String codigoBarras) {
        return consultarUm("SELECT * FROM peca WHERE codigo_barras = ?",
                comando -> comando.setString(1, codigoBarras), PecaRowMapper::map);
    }

    private void vincularCampos(PreparedStatement comando, Peca peca) throws SQLException {
        comando.setString(1, peca.getCodigoBarras());
        comando.setString(2, peca.getFornecedorMarca());
        setNullableInt(comando, 3, peca.getQuantidadeEstoque());
        setNullableDouble(comando, 4, peca.getPrecoCusto());
        setNullableDouble(comando, 5, peca.getPrecoVenda());
        comando.setTimestamp(6, peca.getDataCadastro() == null ? null : Timestamp.valueOf(peca.getDataCadastro()));
        comando.setTimestamp(7, peca.getDataUltimaAtualizacao() == null ? null : Timestamp.valueOf(peca.getDataUltimaAtualizacao()));
        comando.setString(8, peca.getTamanho());
        comando.setString(9, peca.getCor());
        comando.setString(10, peca.getCategoria() == null ? null : peca.getCategoria().name());
    }

    private void setNullableInt(PreparedStatement comando, int indice, Integer valor) throws SQLException {
        if (valor == null) comando.setNull(indice, Types.INTEGER); else comando.setInt(indice, valor);
    }

    private void setNullableDouble(PreparedStatement comando, int indice, Double valor) throws SQLException {
        if (valor == null) comando.setNull(indice, Types.DOUBLE); else comando.setDouble(indice, valor);
    }
}
