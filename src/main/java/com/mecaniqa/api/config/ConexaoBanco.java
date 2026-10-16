package com.mecaniqa.api.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    private static final String URL = valor("DB_URL", "jdbc:mysql://localhost:3306/mecaniqa");
    private static final String USUARIO = valor("DB_USER", "root");
    private static final String SENHA = valor("DB_PASSWORD", "root");

    private ConexaoBanco() {}

    public static Connection getConexao() {
        return getConexao(URL, USUARIO, SENHA);
    }

    public static Connection getConexao(String url, String usuario, String senha) {
        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            if ("28000".equals(e.getSQLState())) {
                throw new IllegalStateException(
                        "Erro ao conectar ao banco de dados: usuário ou senha inválidos.", e);
            }
            throw new IllegalStateException(
                    "Erro ao conectar ao banco de dados: verifique a URL, as credenciais e se o MySQL está ativo. "
                            + e.getMessage(), e);
        }
    }

    private static String valor(String variavel, String padrao) {
        String valor = System.getenv(variavel);
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}
