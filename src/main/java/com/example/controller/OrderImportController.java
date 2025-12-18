package com.example.controller;

import com.example.dto.TinyProperties;
import com.example.service.OrderImportService;
import com.example.service.OrderSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderImportController {

    private final OrderImportService service;
    private final OrderSyncService orderSyncService;
    private final TinyProperties tinyProperties;

    /**
     * Exemplo:
     * GET /api/orders/import?dataInicial=01/12/2024&dataFinal=31/12/2024
     */
    @GetMapping("/import")
    public ResponseEntity<String> importOrders(
            @RequestParam String dataInicial,
            @RequestParam String dataFinal
    ) {

        log.info("Iniciando importação de pedidos Tiny: {} -> {}",
                dataInicial, dataFinal);

        service.importByPeriod(dataInicial, dataFinal);

        return ResponseEntity.ok(
                "Importação de pedidos finalizada com sucesso"
        );
    }

    /**
     * Sincroniza um pedido específico da Tiny pelo ID
     *
     * Ex:
     * POST /orders/sync/906280267
     */
    @PostMapping("/sync/{tinyId}")
    public ResponseEntity<Void> syncOrder(@PathVariable String tinyId) {

        orderSyncService.syncPedido(tinyId, tinyProperties.getToken());

        return ResponseEntity.ok().build();
    }
}

