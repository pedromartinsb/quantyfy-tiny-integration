package com.example.service;

import com.example.client.TinyOrderClient;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.Product;
import com.example.dto.TinyItemDTO;
import com.example.dto.TinyItemWrapperDTO;
import com.example.dto.TinyPedidoDTO;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderSyncService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final TinyOrderClient tinyOrderClient;

    public void syncPedido(String tinyId, String token) {

        TinyPedidoDTO dto = tinyOrderClient
                .obterPedido(tinyId, token)
                .getRetorno()
                .getPedido();

//        Order order = orderRepository
//                .findByTinyId(tinyId)
//                .orElseGet(Order::new);
        Order order = orderRepository.findByIdWithItens(tinyId)
                .orElseThrow();

        order.setTinyId(dto.getId());
        order.setNumero(dto.getNumero());
        order.setNumeroEcommerce(dto.getNumeroEcommerce());
        order.setSituacao(dto.getSituacao());
        order.setFormaEnvio(dto.getFormaEnvio());

        order.setDataPedido(parseDate(dto.getDataPedido()));
        order.setDataFaturamento(parseDate(dto.getDataFaturamento()));
        order.setDataEntrega(parseDate(dto.getDataEntrega()));

        order.setTotalProdutos(new BigDecimal(dto.getTotalProdutos()));
        order.setTotalPedido(new BigDecimal(dto.getTotalPedido()));

        // Itens (replace total)
        if (!order.getItens().isEmpty())
            order.getItens().clear();

        for (TinyItemWrapperDTO wrapper : dto.getItens()) {
            TinyItemDTO itemDTO = wrapper.getItem();

            OrderItem item = new OrderItem();
            item.setOrder(order);
            Product product = productRepository
                    .findByCodigo(itemDTO.getCodigo())
                    .orElseThrow(() -> new RuntimeException(
                            "Produto não encontrado: " + itemDTO.getCodigo()
                    ));
            item.setProduct(product);
            item.setUnidade(itemDTO.getUnidade());
            item.setQuantidade(new BigDecimal(itemDTO.getQuantidade()));
            item.setValorUnitario(new BigDecimal(itemDTO.getValorUnitario()));

            order.getItens().add(item);
        }

        orderRepository.save(order);
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}

