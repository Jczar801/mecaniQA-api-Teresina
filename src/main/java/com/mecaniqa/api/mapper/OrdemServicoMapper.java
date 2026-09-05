package com.mecaniqa.api.mapper;

import com.mecaniqa.api.dto.*;
import com.mecaniqa.api.model.*;
import java.util.List;

public final class OrdemServicoMapper {
    private OrdemServicoMapper() {}

    public static OrdemServico toModel(CriarOrdemServicoDTO dto,
            List<Servico> servicos, List<PedidoPecas> pedidos) {
        OrdemServico.Builder builder = OrdemServico.builder().descricao(dto.getDescricao());
        servicos.forEach(builder::adicionarServico);
        pedidos.forEach(builder::adicionarPedidoPecas);
        return builder.build();
    }

    public static OrdemServicoDTO toDTO(OrdemServico ordem) {
        OrdemServicoDTO dto = new OrdemServicoDTO();
        dto.setCodigo(ordem.getCodigo());
        dto.setDescricao(ordem.getDescricao());
        dto.setStatus(ordem.getStatus());
        dto.setDataCriacao(ordem.getDataCriacao());
        dto.setDataUltimaAtualizacao(ordem.getDataUltimaAtualizacao());
        dto.setServicos(ordem.getServicos().stream().map(ServicoMapper::toDTO).toList());
        dto.setPedidosPecas(ordem.getPedidosPecas().stream().map(PedidoPecasMapper::toDTO).toList());
        return dto;
    }
}
