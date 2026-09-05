package com.mecaniqa.api.repository;

import com.mecaniqa.api.model.OrdemServico;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class OrdemServicoRepository {
    private static final OrdemServicoRepository INSTANCE = new OrdemServicoRepository();
    private final List<OrdemServico> registros = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong();

    private OrdemServicoRepository() {}
    public static OrdemServicoRepository getInstance() { return INSTANCE; }
    public synchronized OrdemServico save(OrdemServico registro) {
        if (registro.getCodigo() == null) {
            registro.setCodigo(sequence.incrementAndGet());
            registros.add(registro);
            return registro;
        }
        for (int i = 0; i < registros.size(); i++) {
            if (registros.get(i).getCodigo().equals(registro.getCodigo())) {
                registros.set(i, registro);
                return registro;
            }
        }
        throw new IllegalArgumentException("Registro inexistente");
    }
    public synchronized Optional<OrdemServico> findById(Long codigo) {
        return registros.stream().filter(r -> r.getCodigo().equals(codigo)).findFirst();
    }
}
