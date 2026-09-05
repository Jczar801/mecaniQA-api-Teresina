package com.mecaniqa.api.controller;

import com.mecaniqa.api.dto.*;
import com.mecaniqa.api.mapper.OrdemServicoMapper;
import com.mecaniqa.api.model.*;
import com.mecaniqa.api.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {
    private final OrdemServicoRepository repository = OrdemServicoRepository.getInstance();
    private final ServicoRepository servicos = ServicoRepository.getInstance();
    private final PedidoPecasRepository pedidos = PedidoPecasRepository.getInstance();

    @PostMapping
    public ResponseEntity<OrdemServicoDTO> criar(@RequestBody CriarOrdemServicoDTO dto) {
        List<Servico> selecionados = ids(dto.getCodigosServicos()).stream().map(codigo ->
                servicos.findById(codigo).orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"))).toList();
        synchronized (pedidos) {
            List<PedidoPecas> materiais = ids(dto.getCodigosPedidosPecas()).stream().map(codigo ->
                    pedidos.findById(codigo).orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"))).toList();
            OrdemServico ordem = OrdemServicoMapper.toModel(dto, selecionados, materiais);
            synchronized (repository) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(OrdemServicoMapper.toDTO(repository.save(ordem)));
            }
        }
    }

    @PatchMapping("/{codigo}/status")
    public ResponseEntity<OrdemServicoDTO> modificarStatus(@PathVariable Long codigo,
            @RequestBody AtualizarStatusOrdemServicoDTO dto) {
        if (dto.getStatus() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status obrigatório");
        synchronized (pedidos) {
            synchronized (repository) {
                OrdemServico ordem = repository.findById(codigo).orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de serviço não encontrada"));
                ordem.setStatus(dto.getStatus());
                ordem.setDataUltimaAtualizacao(LocalDateTime.now());
                return ResponseEntity.ok(OrdemServicoMapper.toDTO(repository.save(ordem)));
            }
        }
    }

    private List<Long> ids(List<Long> codigos) {
        if (codigos == null) return List.of();
        if (codigos.stream().anyMatch(codigo -> codigo == null || codigo <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Códigos devem ser positivos");
        }
        return codigos.stream().distinct().toList();
    }
}
