package com.mecaniqa.api.model;



public class ItemPedido {
    private PedidoPecas pedido;
    private Peca peca;
    private Integer quantidade;

    public ItemPedido() {}
    public PedidoPecas getPedido() { return pedido; }
    public void setPedido(PedidoPecas pedido) { this.pedido = pedido; }
    public Peca getPeca() { return peca; }
    public void setPeca(Peca peca) { this.peca = peca; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
