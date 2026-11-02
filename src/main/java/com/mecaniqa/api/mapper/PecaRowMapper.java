package com.mecaniqa.api.mapper;

import com.mecaniqa.api.exception.ErroAcessoDadosException;
import com.mecaniqa.api.model.CategoriaPeca;
import com.mecaniqa.api.model.Peca;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/** Converte uma linha do ResultSet (tabela peca) em um objeto Peca. */
public final class PecaRowMapper {
    private PecaRowMapper() {}

    public static Peca map(ResultSet linha) {
        try {
            Peca peca = new Peca();
            peca.setCodigo(linha.getLong("codigo"));
            peca.setCodigoBarras(linha.getString("codigo_barras"));
            peca.setFornecedorMarca(linha.getString("fornecedor_marca"));
            peca.setQuantidadeEstoque((Integer) linha.getObject("quantidade_estoque"));
            BigDecimal precoCusto = linha.getBigDecimal("preco_custo");
            peca.setPrecoCusto(precoCusto == null ? null : precoCusto.doubleValue());
            BigDecimal precoVenda = linha.getBigDecimal("preco_venda");
            peca.setPrecoVenda(precoVenda == null ? null : precoVenda.doubleValue());
            Timestamp dataCadastro = linha.getTimestamp("data_cadastro");
            if (dataCadastro != null) peca.setDataCadastro(dataCadastro.toLocalDateTime());
            Timestamp dataAtualizacao = linha.getTimestamp("data_ultima_atualizacao");
            if (dataAtualizacao != null) peca.setDataUltimaAtualizacao(dataAtualizacao.toLocalDateTime());
            peca.setTamanho(linha.getString("tamanho"));
            peca.setCor(linha.getString("cor"));
            String categoria = linha.getString("categoria");
            if (categoria != null) peca.setCategoria(CategoriaPeca.valueOf(categoria));
            return peca;
        } catch (SQLException causa) {
            throw new ErroAcessoDadosException("Não foi possível converter a linha retornada do banco em uma Peça.", causa);
        }
    }
}
