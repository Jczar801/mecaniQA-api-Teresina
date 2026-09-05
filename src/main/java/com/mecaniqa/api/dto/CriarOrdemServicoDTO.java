package com.mecaniqa.api.dto;

import java.util.List;

public class CriarOrdemServicoDTO {
    private String descricao;
    private List<Long> codigosServicos;
    private List<Long> codigosPedidosPecas;

    public CriarOrdemServicoDTO() {}
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public List<Long> getCodigosServicos() { return codigosServicos; }
    public void setCodigosServicos(List<Long> codigosServicos) { this.codigosServicos = codigosServicos; }
    public List<Long> getCodigosPedidosPecas() { return codigosPedidosPecas; }
    public void setCodigosPedidosPecas(List<Long> codigosPedidosPecas) { this.codigosPedidosPecas = codigosPedidosPecas; }
}
