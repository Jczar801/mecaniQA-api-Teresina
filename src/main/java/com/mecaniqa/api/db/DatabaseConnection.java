package com.mecaniqa.api.db;

import com.mecaniqa.api.exception.ErroAcessoDadosException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {
    private static final String ARQUIVO_CONFIGURACAO = "application.properties";

    private DatabaseConnection() {}

    public static Connection obter() {
        Properties configuracao = carregarConfiguracao();
        return obter(configuracao.getProperty("mecaniqa.db.url"),
                configuracao.getProperty("mecaniqa.db.username"),
                configuracao.getProperty("mecaniqa.db.password"));
    }

    public static Connection obter(String url, String usuario, String senha) {
        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException causa) {
            throw new ErroAcessoDadosException(
                    "Não foi possível conectar ao banco de dados. Verifique se o MySQL está no ar e se a URL, "
                            + "o usuário e a senha configurados em application.properties estão corretos.", causa);
        }
    }

    private static Properties carregarConfiguracao() {
        Properties configuracao = new Properties();
        try (InputStream entrada = DatabaseConnection.class.getClassLoader().getResourceAsStream(ARQUIVO_CONFIGURACAO)) {
            if (entrada != null) configuracao.load(entrada);
        } catch (IOException causa) {
            throw new ErroAcessoDadosException("Não foi possível ler as configurações de conexão com o banco de dados.", causa);
        }
        return configuracao;
    }
}
