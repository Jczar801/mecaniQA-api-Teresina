package com.mecaniqa.api.dto;



public class ItemPedidoRequestDTO {
    private Long codigoPeca;
    private Integer quantidade;

    public ItemPedidoRequestDTO() {}
    public Long getCodigoPeca() { return codigoPeca; }
    public void setCodigoPeca(Long codigoPeca) { this.codigoPeca = codigoPeca; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
