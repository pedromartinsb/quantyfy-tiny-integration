package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TinyOrderSearchResponse {

    private Retorno retorno;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Retorno {
        private String status;
        private String status_processamento;
        private Integer pagina;
        private Integer numero_paginas;
        private List<PedidoWrapper> pedidos;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PedidoWrapper {
        private Pedido pedido;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pedido {
        private String id;
        private String numero;
        private String numero_ecommerce;
        private String data_pedido;
        private String nome;
        private BigDecimal valor;
        private String situacao;
    }
}

