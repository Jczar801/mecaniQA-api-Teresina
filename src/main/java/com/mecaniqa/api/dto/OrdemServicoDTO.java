package com.mecaniqa.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.mecaniqa.api.model.StatusOrdemServico;

public class OrdemServicoDTO {
    private Long codigo;
    private String descricao;
    private StatusOrdemServico status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;
    private List<ServicoDTO> servicos;
    private List<PedidoPecasDTO> pedidosPecas;

    public OrdemServicoDTO() {}
    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public StatusOrdemServico getStatus() { return status; }
    public void setStatus(StatusOrdemServico status) { this.status = status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }
    public List<ServicoDTO> getServicos() { return servicos; }
    public void setServicos(List<ServicoDTO> servicos) { this.servicos = servicos; }
    public List<PedidoPecasDTO> getPedidosPecas() { return pedidosPecas; }
    public void setPedidosPecas(List<PedidoPecasDTO> pedidosPecas) { this.pedidosPecas = pedidosPecas; }
}
