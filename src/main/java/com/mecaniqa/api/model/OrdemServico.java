package com.mecaniqa.api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrdemServico {
    private Long codigo;
    private final String descricao;
    private StatusOrdemServico status;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;
    private final List<Servico> servicos;
    private final List<PedidoPecas> pedidosPecas;

    private OrdemServico(Builder builder) {
        descricao = builder.descricao;
        status = StatusOrdemServico.ABERTO;
        dataCriacao = LocalDateTime.now();
        dataUltimaAtualizacao = dataCriacao;
        servicos = List.copyOf(builder.servicos);
        pedidosPecas = List.copyOf(builder.pedidosPecas);
    }

    public static Builder builder() { return new Builder(); }
    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public String getDescricao() { return descricao; }
    public StatusOrdemServico getStatus() { return status; }
    public void setStatus(StatusOrdemServico status) { this.status = Objects.requireNonNull(status); }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime data) { dataUltimaAtualizacao = data; }
    public List<Servico> getServicos() { return servicos; }
    public List<PedidoPecas> getPedidosPecas() { return pedidosPecas; }

    public static class Builder {
        private String descricao;
        private final List<Servico> servicos = new ArrayList<>();
        private final List<PedidoPecas> pedidosPecas = new ArrayList<>();

        public Builder() {}
        public Builder descricao(String descricao) { this.descricao = descricao; return this; }
        public Builder adicionarServico(Servico servico) {
            servicos.add(Objects.requireNonNull(servico));
            return this;
        }
        public Builder adicionarPedidoPecas(PedidoPecas pedido) {
            pedidosPecas.add(Objects.requireNonNull(pedido));
            return this;
        }
        public OrdemServico build() { return new OrdemServico(this); }
    }
}
