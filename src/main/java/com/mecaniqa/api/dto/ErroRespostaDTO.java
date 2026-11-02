package com.mecaniqa.api.dto;

public class ErroRespostaDTO {
    private String mensagem;

    public ErroRespostaDTO() {}
    public ErroRespostaDTO(String mensagem) { this.mensagem = mensagem; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}
