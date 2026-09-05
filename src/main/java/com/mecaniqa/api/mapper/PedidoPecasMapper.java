package com.mecaniqa.api.mapper;

import com.mecaniqa.api.dto.*;
import com.mecaniqa.api.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public final class PedidoPecasMapper {
    private PedidoPecasMapper() {}

    public static PedidoPecas toModel(CriarPedidoPecasDTO dto) {
        PedidoPecas pedido = new PedidoPecas();
        pedido.setStatus(StatusPedidoPecas.ORCANDO);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setDataUltimaAtualizacao(pedido.getDataCriacao());
        pedido.setItens(new ArrayList<>());
        return pedido;
    }

    public static ItemPedido toModel(ItemPedidoRequestDTO dto, Peca peca, PedidoPecas pedido) {
        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setPeca(peca);
        item.setQuantidade(dto.getQuantidade());
        return item;
    }

    public static PedidoPecasDTO toDTO(PedidoPecas pedido) {
        PedidoPecasDTO dto = new PedidoPecasDTO();
        dto.setCodigo(pedido.getCodigo());
        dto.setStatus(pedido.getStatus());
        dto.setDataCriacao(pedido.getDataCriacao());
        dto.setDataUltimaAtualizacao(pedido.getDataUltimaAtualizacao());
        dto.setItens(pedido.getItens().stream().map(item -> {
            ItemPedidoDTO resultado = new ItemPedidoDTO();
            resultado.setCodigoPeca(item.getPeca().getCodigo());
            resultado.setQuantidade(item.getQuantidade());
            return resultado;
        }).toList());
        return dto;
    }
}
