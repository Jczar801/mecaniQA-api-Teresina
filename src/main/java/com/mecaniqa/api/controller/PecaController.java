package com.mecaniqa.api.controller;

import com.mecaniqa.api.model.Peca;
import com.mecaniqa.api.repository.PecaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pecas")
public class PecaController {
    private final PecaRepository repository = PecaRepository.getInstance();

    @PostMapping
    public ResponseEntity<Peca> criar(@RequestBody Peca peca) {
        if (peca.getCategoria() == null) return ResponseEntity.badRequest().build();
        peca.setCodigo(null); // garante semântica de criação, ignora qualquer codigo enviado pelo cliente
        LocalDateTime agora = LocalDateTime.now();
        peca.setDataCadastro(agora);
        peca.setDataUltimaAtualizacao(agora);
        return ResponseEntity.status(201).body(repository.save(peca));
    }

    @GetMapping
    public ResponseEntity<List<Peca>> listar() { return ResponseEntity.ok(repository.findAll()); }

    @GetMapping("/{codigo}")
    public ResponseEntity<Peca> buscar(@PathVariable Long codigo) {
        return repository.findById(codigo).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Peca> atualizar(@PathVariable Long codigo, @RequestBody Peca nova) {
        return repository.findById(codigo).map(atual -> {
            nova.setCodigo(codigo);
            nova.setDataCadastro(atual.getDataCadastro());
            nova.setDataUltimaAtualizacao(LocalDateTime.now());
            return ResponseEntity.ok(repository.save(nova));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluir(@PathVariable Long codigo) {
        if (!repository.deleteById(codigo)) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
