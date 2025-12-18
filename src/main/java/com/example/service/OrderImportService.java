package com.example.service;

import com.example.client.TinyOrderClient;
import com.example.domain.Order;
import com.example.dto.TinyOrderSearchResponse;
import com.example.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderImportService {

    private final TinyOrderClient client;
    private final OrderRepository repository;

    @Transactional
    public void importByPeriod(String dataInicial, String dataFinal) {

        int page = 1;
        int totalPages;

        do {
            log.info("Buscando pedidos página {}", page);

            TinyOrderSearchResponse response =
                    client.searchOrders(page, dataInicial, dataFinal);

            var retorno = response.getRetorno();
            totalPages = retorno.getNumero_paginas();

            if (retorno.getPedidos() != null) {
                for (var wrapper : retorno.getPedidos()) {
                    saveOrder(wrapper.getPedido());
                }
            }

            page++;

            // 🛑 proteção contra rate limit
            sleep(400);

        } while (page <= totalPages);
    }

    private void saveOrder(TinyOrderSearchResponse.Pedido tiny) {

        log.info("Processando pedido Tiny ID: {}", tiny.getId());

        if (repository.existsByTinyId(tiny.getId())) {
            return;
        }

        Order order = new Order();
        order.setTinyId(tiny.getId());
        order.setNumero(tiny.getNumero());
        order.setNumeroEcommerce(tiny.getNumero_ecommerce());
        order.setNomeCliente(tiny.getNome());
        order.setValor(tiny.getValor());
        order.setSituacao(tiny.getSituacao());
        order.setDataPedido(LocalDate.parse(
                tiny.getData_pedido(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        ));

        repository.save(order);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
