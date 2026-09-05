package com.mecaniqa.api.dto;

import com.mecaniqa.api.model.StatusPedidoPecas;

public class AtualizarStatusPedidoPecasDTO {
    private StatusPedidoPecas status;

    public AtualizarStatusPedidoPecasDTO() {}
    public StatusPedidoPecas getStatus() { return status; }
    public void setStatus(StatusPedidoPecas status) { this.status = status; }
}
