package com.mecaniqa.api.dto;

import java.util.List;

public class CriarPedidoPecasDTO {
    private List<ItemPedidoRequestDTO> itens;

    public CriarPedidoPecasDTO() {}
    public List<ItemPedidoRequestDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoRequestDTO> itens) { this.itens = itens; }
}
