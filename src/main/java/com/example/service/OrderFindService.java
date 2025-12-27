package com.example.service;

import com.example.domain.Order;
import com.example.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFindService {

    private final OrderRepository orderRepository;

    public List<Order> findAllByDataPedidoBetween(String dataInicial, String dataFinal) {

        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataInicialFormatter = LocalDate.parse(dataInicial, customFormatter);
        LocalDate dataFinalFormatter = LocalDate.parse(dataFinal, customFormatter);

        var orders = orderRepository.findAllByDataPedidoBetween(dataInicialFormatter, dataFinalFormatter);
        orders.sort(Comparator.comparing(Order::getDataPedido));

        return orders.stream()
                .filter(order -> order.getSituacao().equals("Entregue"))
                .toList();
    }
}
