package com.example.service;

import com.example.client.TinyOrderClient;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.Product;
import com.example.dto.*;
import com.example.exception.TinyRateLimitException;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.utils.DateUtils.parseDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSyncService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final TinyOrderClient tinyOrderClient;
    private final TinyProperties tinyProperties;

    public void enrichPedidos(String dataInicial, String dataFinal) {

//        List<Order> orders = orderRepository.findAll();
//        List<Order> orders = orderRepository.findAllByDataEntregaIsNull();
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataInicialFormatter = LocalDate.parse(dataInicial, customFormatter);
        LocalDate dataFinalFormatter = LocalDate.parse(dataFinal, customFormatter);
        List<Order> orders = orderRepository.findByDataPedidoBetween(dataInicialFormatter, dataFinalFormatter);

        for (Order order : orders) {
            try {
                this.enrich(order.getTinyId());

                Thread.sleep(800);

            } catch (TinyRateLimitException e) {
                log.error("⛔ Rate limit Tiny atingido. Abortando enriquecimento.");
                break;

            } catch (Exception e) {
                log.error("Erro ao enriquecer pedido {}: {}", order.getId(), e.getMessage());
            }
        }
    }

    public void enrich(String tinyId) {

        TinyPedidoDTO dto = tinyOrderClient
                .obterPedido(tinyId, tinyProperties.getToken())
                .getRetorno()
                .getPedido();

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
}

