package com.mecaniqa.api.repository;

import com.mecaniqa.api.model.Peca;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class PecaRepository {
    private static final PecaRepository INSTANCE = new PecaRepository();
    private final List<Peca> pecas = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong();

    private PecaRepository() {}
    public static PecaRepository getInstance() { return INSTANCE; }

    public synchronized Peca save(Peca peca) {
        if (peca.getCodigo() == null) {
            peca.setCodigo(sequence.incrementAndGet());
            pecas.add(peca);
            return peca;
        }
        for (int i = 0; i < pecas.size(); i++) {
            if (pecas.get(i).getCodigo().equals(peca.getCodigo())) {
                pecas.set(i, peca);
                return peca;
            }
        }
        return null;
    }

    public synchronized List<Peca> findAll() { return new ArrayList<>(pecas); }
    public synchronized Optional<Peca> findById(Long codigo) {
        return pecas.stream().filter(p -> p.getCodigo().equals(codigo)).findFirst();
    }
    public synchronized boolean deleteById(Long codigo) {
        return pecas.removeIf(p -> p.getCodigo().equals(codigo));
    }
}
