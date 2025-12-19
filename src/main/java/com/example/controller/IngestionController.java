package com.example.controller;

import com.example.service.ProductDetailIngestionService;
import com.example.service.ProductIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingestion/products")
@RequiredArgsConstructor
public class IngestionController {

    private final ProductIngestionService service;
    private final ProductDetailIngestionService detailService;

    @PostMapping
    public ResponseEntity<Void> ingest() {
        service.ingestAllProducts();
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/details")
    public ResponseEntity<Void> enrichDetails() {
        detailService.enrichProductsDetails();
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/stocks")
    public ResponseEntity<Void> enrichStocks() {
        detailService.enrichProductsStocks();
        return ResponseEntity.accepted().build();
    }
}
