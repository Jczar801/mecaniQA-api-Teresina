package com.mecaniqa.api;

import com.mecaniqa.api.db.DatabaseConnection;
import com.mecaniqa.api.repository.PecaRepository;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Cenário de teste (23/10): garantir que as conexões sempre estão sendo
 * encerradas, independentemente de a consulta falhar ou obter sucesso
 * (ciclo de vida padronizado pelo Template Method em RepositorioJdbcBase).
 */
class Oat3TemplateMethodTest {
    private final PecaRepository pecas = PecaRepository.getInstance();

    @Test void naoDeixaConexoesAbertasAposConsultasComESemResultado() throws Exception {
        long conexoesAntes = conexoesAtivas();

        for (int i = 0; i < 10; i++) pecas.findById(999_000_000L + i); // nunca encontra -> caminho "sem resultado"
        for (int i = 0; i < 10; i++) pecas.findAll(); // caminho "com resultado"

        long conexoesDepois = conexoesAtivas();
        assertEquals(conexoesAntes, conexoesDepois,
                "cada chamada ao repositório deve abrir e fechar sua própria conexão (try-with-resources no Template Method)");
    }

    private long conexoesAtivas() throws Exception {
        try (Connection conexao = DatabaseConnection.obter();
             Statement comando = conexao.createStatement();
             ResultSet resultado = comando.executeQuery("SHOW STATUS LIKE 'Threads_connected'")) {
            resultado.next();
            return resultado.getLong("Value");
        }
    }
}
