package com.mecaniqa.api.controller;

import com.mecaniqa.api.model.Peca;
import com.mecaniqa.api.dto.PecaDTO;
import com.mecaniqa.api.mapper.PecaMapper;
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
    public ResponseEntity<PecaDTO> criar(@RequestBody PecaDTO dto) {
        Peca peca = PecaMapper.toModel(dto);
        if (peca.getCategoria() == null) return ResponseEntity.badRequest().build();
        peca.setCodigo(null); // garante semântica de criação, ignora qualquer codigo enviado pelo cliente
        LocalDateTime agora = LocalDateTime.now();
        peca.setDataCadastro(agora);
        peca.setDataUltimaAtualizacao(agora);
        return ResponseEntity.status(201).body(PecaMapper.toDTO(repository.save(peca)));
    }

    @GetMapping
    public ResponseEntity<List<PecaDTO>> listar() { return ResponseEntity.ok(repository.findAll().stream().map(PecaMapper::toDTO).toList()); }

    @GetMapping("/{codigo}")
    public ResponseEntity<PecaDTO> buscar(@PathVariable Long codigo) {
        return repository.findById(codigo).map(PecaMapper::toDTO).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<PecaDTO> atualizar(@PathVariable Long codigo, @RequestBody PecaDTO dto) {
        Peca nova = PecaMapper.toModel(dto);
        return repository.findById(codigo).map(atual -> {
            nova.setCodigo(codigo);
            nova.setDataCadastro(atual.getDataCadastro());
            nova.setDataUltimaAtualizacao(LocalDateTime.now());
            return ResponseEntity.ok(PecaMapper.toDTO(repository.save(nova)));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluir(@PathVariable Long codigo) {
        if (!repository.deleteById(codigo)) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
