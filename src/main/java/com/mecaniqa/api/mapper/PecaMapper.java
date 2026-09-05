package com.mecaniqa.api.mapper;

import com.mecaniqa.api.dto.PecaDTO;
import com.mecaniqa.api.model.Peca;

public final class PecaMapper {
    private PecaMapper() {}
    public static Peca toModel(PecaDTO origem) {
        Peca destino = new Peca();
        destino.setCodigoBarras(origem.getCodigoBarras());
        destino.setFornecedorMarca(origem.getFornecedorMarca());
        destino.setQuantidadeEstoque(origem.getQuantidadeEstoque());
        destino.setPrecoCusto(origem.getPrecoCusto());
        destino.setPrecoVenda(origem.getPrecoVenda());
        destino.setTamanho(origem.getTamanho());
        destino.setCor(origem.getCor());
        destino.setCategoria(origem.getCategoria());
        return destino;
    }
    public static PecaDTO toDTO(Peca origem) {
        PecaDTO destino = new PecaDTO();
        destino.setCodigo(origem.getCodigo());
        destino.setCodigoBarras(origem.getCodigoBarras());
        destino.setFornecedorMarca(origem.getFornecedorMarca());
        destino.setQuantidadeEstoque(origem.getQuantidadeEstoque());
        destino.setPrecoCusto(origem.getPrecoCusto());
        destino.setPrecoVenda(origem.getPrecoVenda());
        destino.setDataCadastro(origem.getDataCadastro());
        destino.setDataUltimaAtualizacao(origem.getDataUltimaAtualizacao());
        destino.setTamanho(origem.getTamanho());
        destino.setCor(origem.getCor());
        destino.setCategoria(origem.getCategoria());
        return destino;
    }
}
