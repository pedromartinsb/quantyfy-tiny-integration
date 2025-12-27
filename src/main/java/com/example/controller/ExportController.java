package com.example.controller;

import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.service.OrderFindService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static io.netty.util.internal.StringUtil.escapeCsv;

@Slf4j
@RestController
@RequestMapping("/exports")
@RequiredArgsConstructor
public class ExportController {

    private final OrderFindService orderFindService;

    @GetMapping("/orders")
    public void exportOrders(@RequestParam(required = false) String dataInicial,
                             @RequestParam(required = false) String dataFinal,
                             HttpServletResponse response) throws IOException {

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"sales.csv\"");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("TinyId,Numero,Valor,DataPedido,Quantidade,TotalProdutos,TotalPedido,Situacao");

            List<Order> orders = orderFindService.findAllByDataPedidoBetween(dataInicial, dataFinal);
            log.info("Orders size: {}", orders.size());

            DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Order order : orders) {

                BigDecimal totalProdutos = order.getTotalProdutos() != null ? order.getTotalProdutos() : BigDecimal.ZERO;
                BigDecimal totalPedido = order.getTotalPedido() != null ? order.getTotalPedido() : BigDecimal.ZERO;

                int quantidade = 0;

                for (OrderItem orderItem : order.getItens()) {
                    quantidade += orderItem.getQuantidade().intValue();
                }

                String row = String.format(Locale.ROOT, "%s,%s,%.2f,%s,%d,%.2f,%.2f,%s",
                        order.getTinyId(),
                        escapeCsv(order.getNumero()),
                        order.getValor(),
                        order.getDataPedido().format(customFormatter),
                        quantidade,
                        totalProdutos,
                        totalPedido,
                        escapeCsv(order.getSituacao()));
                writer.println(row);
            }
        }
    }
}
