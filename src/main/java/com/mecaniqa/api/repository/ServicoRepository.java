package com.mecaniqa.api.repository;

import com.mecaniqa.api.model.Servico;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class ServicoRepository {
    private static final ServicoRepository INSTANCE = new ServicoRepository();
    private final List<Servico> servicos = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong();

    private ServicoRepository() {}
    public static ServicoRepository getInstance() { return INSTANCE; }

    public synchronized Servico save(Servico servico) {
        if (servico.getCodigo() == null) {
            servico.setCodigo(sequence.incrementAndGet());
            servicos.add(servico);
            return servico;
        }
        for (int i = 0; i < servicos.size(); i++) {
            if (servicos.get(i).getCodigo().equals(servico.getCodigo())) {
                servicos.set(i, servico);
                return servico;
            }
        }
        return null;
    }

    public synchronized List<Servico> findAll() { return new ArrayList<>(servicos); }
    public synchronized Optional<Servico> findById(Long codigo) {
        return servicos.stream().filter(s -> s.getCodigo().equals(codigo)).findFirst();
    }
    public synchronized boolean deleteById(Long codigo) {
        return servicos.removeIf(s -> s.getCodigo().equals(codigo));
    }
}
