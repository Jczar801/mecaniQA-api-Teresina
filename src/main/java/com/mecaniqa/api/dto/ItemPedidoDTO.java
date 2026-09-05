package com.mecaniqa.api.dto;



public class ItemPedidoDTO {
    private Long codigoPeca;
    private Integer quantidade;

    public ItemPedidoDTO() {}
    public Long getCodigoPeca() { return codigoPeca; }
    public void setCodigoPeca(Long codigoPeca) { this.codigoPeca = codigoPeca; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
