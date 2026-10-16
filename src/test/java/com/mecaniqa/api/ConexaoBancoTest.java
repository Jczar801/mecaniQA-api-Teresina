package com.mecaniqa.api;

import com.mecaniqa.api.config.ConexaoBanco;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConexaoBancoTest {
    @Test void lancaErroClaroQuandoCredenciaisEstaoErradas() {
        IllegalStateException erro = assertThrows(IllegalStateException.class,
                () -> ConexaoBanco.getConexao("jdbc:mysql://localhost:3306/mecaniqa", "usuario_errado", "senha_errada"));
        assertTrue(erro.getMessage().startsWith("Erro ao conectar ao banco de dados"));
        assertNotNull(erro.getCause());
    }
}
