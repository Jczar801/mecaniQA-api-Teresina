package com.mecaniqa.api.controller;

import com.mecaniqa.api.dto.*;
import com.mecaniqa.api.mapper.PedidoPecasMapper;
import com.mecaniqa.api.model.*;
import com.mecaniqa.api.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/pedidos-pecas")
public class PedidoPecasController {
    private final PedidoPecasRepository repository = PedidoPecasRepository.getInstance();
    private final PecaRepository pecas = PecaRepository.getInstance();

    @PostMapping
    public ResponseEntity<PedidoPecasDTO> criar(@RequestBody CriarPedidoPecasDTO dto) {
        PedidoPecas pedido = PedidoPecasMapper.toModel(dto);
        if (dto.getItens() != null) {
            for (ItemPedidoRequestDTO item : dto.getItens()) adicionarItem(pedido, item);
        }
        synchronized (repository) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(PedidoPecasMapper.toDTO(repository.save(pedido)));
        }
    }

    @PostMapping("/{codigo}/itens")
    public ResponseEntity<PedidoPecasDTO> adicionarPeca(@PathVariable Long codigo,
            @RequestBody ItemPedidoRequestDTO dto) {
        synchronized (repository) {
            PedidoPecas pedido = buscar(codigo);
            adicionarItem(pedido, dto);
            pedido.setDataUltimaAtualizacao(LocalDateTime.now());
            return ResponseEntity.ok(PedidoPecasMapper.toDTO(repository.save(pedido)));
        }
    }

    @PatchMapping("/{codigo}/status")
    public ResponseEntity<PedidoPecasDTO> modificarStatus(@PathVariable Long codigo,
            @RequestBody AtualizarStatusPedidoPecasDTO dto) {
        if (dto.getStatus() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status obrigatório");
        synchronized (repository) {
            PedidoPecas pedido = buscar(codigo);
            pedido.setStatus(dto.getStatus());
            pedido.setDataUltimaAtualizacao(LocalDateTime.now());
            return ResponseEntity.ok(PedidoPecasMapper.toDTO(repository.save(pedido)));
        }
    }

    private PedidoPecas buscar(Long codigo) {
        return repository.findById(codigo).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    private void adicionarItem(PedidoPecas pedido, ItemPedidoRequestDTO dto) {
        if (dto == null || dto.getCodigoPeca() == null || dto.getCodigoPeca() <= 0
                || dto.getQuantidade() == null || dto.getQuantidade() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Peça e quantidade positiva obrigatórias");
        }
        Peca peca = pecas.findById(dto.getCodigoPeca()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Peça não encontrada"));
        for (ItemPedido item : pedido.getItens()) {
            if (item.getPeca().getCodigo().equals(dto.getCodigoPeca())) {
                long quantidade = (long) item.getQuantidade() + dto.getQuantidade();
                if (quantidade > Integer.MAX_VALUE) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade excede o limite");
                }
                item.setQuantidade((int) quantidade);
                return;
            }
        }
        pedido.getItens().add(PedidoPecasMapper.toModel(dto, peca, pedido));
    }
}
