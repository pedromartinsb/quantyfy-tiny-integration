package com.example.service;

import com.example.client.TinyApiClient;
import com.example.domain.Product;
import com.example.repository.ProductRepository;
import com.example.dto.TinyProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductIngestionService {

    private final TinyApiClient client;
    private final ProductRepository repository;
    private final Executor taskExecutor;

    @Transactional
    public void ingestAllProducts() {
        TinyProductResponse firstPage = client.searchProducts(1).block();
        int totalPages = firstPage.getRetorno().getNumero_paginas();

        List<CompletableFuture<Void>> futures = IntStream.rangeClosed(1, totalPages)
                .mapToObj(page -> CompletableFuture.runAsync(() -> ingestPage(page), taskExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void ingestPage(int page) {
        client.searchProducts(page)
                .block()
                .getRetorno()
                .getProdutos()
                .forEach(this::processProduct);
    }

    private void processProduct(TinyProductResponse.ProdutoWrapper wrapper) {

        TinyProductResponse.Produto tiny = wrapper.getProduto();

        if (tiny == null || tiny.getId() == null) {
            log.warn("Produto inválido retornado pela Tiny: {}", wrapper);
            throw new IllegalStateException("Produto Tiny sem ID");
        }

        Product product = repository.findByTinyId(tiny.getId())
                .orElseGet(Product::new);

        product.setTinyId(tiny.getId());
        product.setNome(tiny.getNome());
        product.setCodigo(tiny.getCodigo());
        product.setPreco(tiny.getPreco());

        repository.save(product);
        log.debug("Salvando produto Tiny id={}, nome={}", tiny.getId(), tiny.getNome());
    }
}
