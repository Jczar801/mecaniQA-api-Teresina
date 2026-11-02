package com.mecaniqa.api.repository;

import com.mecaniqa.api.db.DatabaseConnection;
import com.mecaniqa.api.exception.ErroAcessoDadosException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Template Method: padroniza o ciclo de vida de uma operação JDBC (abrir conexão,
 * preparar o comando, executar e fechar) para que os repositórios concretos não
 * repitam o mesmo bloco de abertura/try/catch/finally em cada método de CRUD.
 * As conexões e comandos são sempre fechados (try-with-resources), mesmo quando
 * a consulta falha.
 */
public abstract class RepositorioJdbcBase<T> {

    protected final <R> List<R> consultar(String sql, PreparadorDeParametros preparador, ConversorDeLinha<R> conversor) {
        try (Connection conexao = DatabaseConnection.obter();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preparador.preparar(comando);
            List<R> resultados = new ArrayList<>();
            try (ResultSet linhas = comando.executeQuery()) {
                while (linhas.next()) resultados.add(conversor.converter(linhas));
            }
            return resultados;
        } catch (SQLException causa) {
            throw new ErroAcessoDadosException("Falha ao consultar dados no banco de dados.", causa);
        }
    }

    protected final <R> Optional<R> consultarUm(String sql, PreparadorDeParametros preparador, ConversorDeLinha<R> conversor) {
        List<R> resultados = consultar(sql, preparador, conversor);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
    }

    protected final int executarAtualizacao(String sql, PreparadorDeParametros preparador) {
        try (Connection conexao = DatabaseConnection.obter();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            preparador.preparar(comando);
            return comando.executeUpdate();
        } catch (SQLException causa) {
            throw new ErroAcessoDadosException("Falha ao atualizar dados no banco de dados.", causa);
        }
    }

    protected final long executarInsercao(String sql, PreparadorDeParametros preparador) {
        try (Connection conexao = DatabaseConnection.obter();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparador.preparar(comando);
            comando.executeUpdate();
            try (ResultSet chaves = comando.getGeneratedKeys()) {
                return chaves.next() ? chaves.getLong(1) : 0L;
            }
        } catch (SQLException causa) {
            throw new ErroAcessoDadosException("Falha ao inserir dados no banco de dados.", causa);
        }
    }

    /** Parte variável que cada repositório concreto fornece: como vincular os parâmetros do comando SQL. */
    @FunctionalInterface
    protected interface PreparadorDeParametros {
        void preparar(PreparedStatement comando) throws SQLException;
    }

    /** Parte variável que cada repositório concreto fornece: como converter uma linha do ResultSet em objeto Java. */
    @FunctionalInterface
    protected interface ConversorDeLinha<R> {
        R converter(ResultSet linha) throws SQLException;
    }
}
