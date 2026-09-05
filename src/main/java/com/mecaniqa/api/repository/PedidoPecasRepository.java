package com.mecaniqa.api.repository;

import com.mecaniqa.api.model.PedidoPecas;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class PedidoPecasRepository {
    private static final PedidoPecasRepository INSTANCE = new PedidoPecasRepository();
    private final List<PedidoPecas> registros = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong();

    private PedidoPecasRepository() {}
    public static PedidoPecasRepository getInstance() { return INSTANCE; }
    public synchronized PedidoPecas save(PedidoPecas registro) {
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
    public synchronized Optional<PedidoPecas> findById(Long codigo) {
        return registros.stream().filter(r -> r.getCodigo().equals(codigo)).findFirst();
    }
}
