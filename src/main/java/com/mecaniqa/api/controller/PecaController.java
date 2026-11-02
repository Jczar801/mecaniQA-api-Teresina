package com.mecaniqa.api.controller;

import com.mecaniqa.api.model.Peca;
import com.mecaniqa.api.dto.ErroRespostaDTO;
import com.mecaniqa.api.dto.PecaDTO;
import com.mecaniqa.api.exception.ErroAcessoDadosException;
import com.mecaniqa.api.exception.RecursoNaoEncontradoException;
import com.mecaniqa.api.mapper.PecaMapper;
import com.mecaniqa.api.repository.PecaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/pecas")
public class PecaController {
    private final PecaRepository repository = PecaRepository.getInstance();

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody PecaDTO dto) {
        try {
            Peca peca = PecaMapper.toModel(dto);
            if (peca.getCategoria() == null) return ResponseEntity.badRequest().build();
            peca.setCodigo(null); // garante semântica de criação, ignora qualquer codigo enviado pelo cliente
            LocalDateTime agora = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
            peca.setDataCadastro(agora);
            peca.setDataUltimaAtualizacao(agora);
            return ResponseEntity.status(201).body(PecaMapper.toDTO(repository.save(peca)));
        } catch (ErroAcessoDadosException causa) {
            return erroBancoDeDados(causa);
        }
    }

    @GetMapping
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(repository.findAll().stream().map(PecaMapper::toDTO).toList());
        } catch (ErroAcessoDadosException causa) {
            return erroBancoDeDados(causa);
        }
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Object> buscar(@PathVariable Long codigo) {
        try {
            Peca peca = repository.findById(codigo).orElseThrow(RecursoNaoEncontradoException::new);
            return ResponseEntity.ok(PecaMapper.toDTO(peca));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        } catch (ErroAcessoDadosException causa) {
            return erroBancoDeDados(causa);
        }
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Object> atualizar(@PathVariable Long codigo, @RequestBody PecaDTO dto) {
        try {
            Peca atual = repository.findById(codigo).orElse(null);
            if (atual == null) return ResponseEntity.notFound().build();
            Peca nova = PecaMapper.toModel(dto);
            nova.setCodigo(codigo);
            nova.setDataCadastro(atual.getDataCadastro());
            nova.setDataUltimaAtualizacao(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            return ResponseEntity.ok(PecaMapper.toDTO(repository.save(nova)));
        } catch (ErroAcessoDadosException causa) {
            return erroBancoDeDados(causa);
        }
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Object> excluir(@PathVariable Long codigo) {
        try {
            if (!repository.deleteById(codigo)) return ResponseEntity.notFound().build();
            return ResponseEntity.noContent().build();
        } catch (ErroAcessoDadosException causa) {
            return erroBancoDeDados(causa);
        }
    }

    private ResponseEntity<Object> erroBancoDeDados(ErroAcessoDadosException causa) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErroRespostaDTO(causa.getMessage()));
    }
}
