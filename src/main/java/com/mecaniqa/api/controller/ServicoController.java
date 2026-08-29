package com.mecaniqa.api.controller;

import com.mecaniqa.api.model.Servico;
import com.mecaniqa.api.repository.ServicoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {
    private final ServicoRepository repository = ServicoRepository.getInstance();

    @PostMapping
    public ResponseEntity<Servico> criar(@RequestBody Servico servico) {
        servico.setCodigo(null); // garante semântica de criação, ignora qualquer codigo enviado pelo cliente
        LocalDateTime agora = LocalDateTime.now();
        servico.setDataCriacao(agora);
        servico.setDataUltimaAtualizacao(agora);
        return ResponseEntity.status(201).body(repository.save(servico));
    }

    @GetMapping
    public ResponseEntity<List<Servico>> listar() { return ResponseEntity.ok(repository.findAll()); }

    @GetMapping("/{codigo}")
    public ResponseEntity<Servico> buscar(@PathVariable Long codigo) {
        return repository.findById(codigo).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Servico> atualizar(@PathVariable Long codigo, @RequestBody Servico novo) {
        return repository.findById(codigo).map(atual -> {
            novo.setCodigo(codigo);
            novo.setDataCriacao(atual.getDataCriacao());
            novo.setDataUltimaAtualizacao(LocalDateTime.now());
            return ResponseEntity.ok(repository.save(novo));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluir(@PathVariable Long codigo) {
        if (!repository.deleteById(codigo)) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
