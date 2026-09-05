package com.mecaniqa.api.mapper;

import com.mecaniqa.api.dto.ServicoDTO;
import com.mecaniqa.api.model.Servico;

public final class ServicoMapper {
    private ServicoMapper() {}
    public static Servico toModel(ServicoDTO origem) {
        Servico destino = new Servico();
        destino.setNome(origem.getNome());
        destino.setTempoEstimadoMinutos(origem.getTempoEstimadoMinutos());
        destino.setCustoTabelado(origem.getCustoTabelado());
        return destino;
    }
    public static ServicoDTO toDTO(Servico origem) {
        ServicoDTO destino = new ServicoDTO();
        destino.setCodigo(origem.getCodigo());
        destino.setNome(origem.getNome());
        destino.setTempoEstimadoMinutos(origem.getTempoEstimadoMinutos());
        destino.setCustoTabelado(origem.getCustoTabelado());
        destino.setDataCriacao(origem.getDataCriacao());
        destino.setDataUltimaAtualizacao(origem.getDataUltimaAtualizacao());
        return destino;
    }
}
