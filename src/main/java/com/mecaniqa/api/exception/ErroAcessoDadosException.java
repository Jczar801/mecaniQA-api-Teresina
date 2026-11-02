package com.mecaniqa.api.exception;

public class ErroAcessoDadosException extends RuntimeException {
    public ErroAcessoDadosException(String mensagem) {
        super(mensagem);
    }

    public ErroAcessoDadosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
