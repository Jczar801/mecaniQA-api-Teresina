package com.mecaniqa.api.dto;

import com.mecaniqa.api.model.StatusOrdemServico;

public class AtualizarStatusOrdemServicoDTO {
    private StatusOrdemServico status;

    public AtualizarStatusOrdemServicoDTO() {}
    public StatusOrdemServico getStatus() { return status; }
    public void setStatus(StatusOrdemServico status) { this.status = status; }
}
