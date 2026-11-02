package com.mecaniqa.api.controller;

import com.mecaniqa.api.model.Servico;
import com.mecaniqa.api.dto.ErroRespostaDTO;
import com.mecaniqa.api.dto.ServicoDTO;
import com.mecaniqa.api.exception.ErroAcessoDadosException;
import com.mecaniqa.api.exception.RecursoNaoEncontradoException;
import com.mecaniqa.api.mapper.ServicoMapper;
import com.mecaniqa.api.repository.ServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {
    private final ServicoRepository repository = ServicoRepository.getInstance();

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody ServicoDTO dto) {
        try {
            Servico servico = ServicoMapper.toModel(dto);
            servico.setCodigo(null); // garante semântica de criação, ignora qualquer codigo enviado pelo cliente
            LocalDateTime agora = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
            servico.setDataCriacao(agora);
            servico.setDataUltimaAtualizacao(agora);
            return ResponseEntity.status(201).body(ServicoMapper.toDTO(repository.save(servico)));
        } catch (ErroAcessoDadosException causa) {
            return erroBancoDeDados(causa);
        }
    }

    @GetMapping
    public ResponseEntity<Object> listar() {
        try {
            return ResponseEntity.ok(repository.findAll().stream().map(ServicoMapper::toDTO).toList());
        } catch (ErroAcessoDadosException causa) {
            return erroBancoDeDados(causa);
        }
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Object> buscar(@PathVariable Long codigo) {
        try {
            Servico servico = repository.findById(codigo).orElseThrow(RecursoNaoEncontradoException::new);
            return ResponseEntity.ok(ServicoMapper.toDTO(servico));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        } catch (ErroAcessoDadosException causa) {
            return erroBancoDeDados(causa);
        }
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Object> atualizar(@PathVariable Long codigo, @RequestBody ServicoDTO dto) {
        try {
            Servico atual = repository.findById(codigo).orElse(null);
            if (atual == null) return ResponseEntity.notFound().build();
            Servico novo = ServicoMapper.toModel(dto);
            novo.setCodigo(codigo);
            novo.setDataCriacao(atual.getDataCriacao());
            novo.setDataUltimaAtualizacao(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
            return ResponseEntity.ok(ServicoMapper.toDTO(repository.save(novo)));
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
