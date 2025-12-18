package com.example.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TinyProductResponse {

    private Retorno retorno;

    @Data
    public static class Retorno {
        private String status;
        private Integer pagina;
        private Integer numero_paginas;
        private List<ProdutoWrapper> produtos;
    }

    @Data
    public static class ProdutoWrapper {
        private Produto produto;
    }

    @Data
    public static class Produto {
        private String id;
        private String nome;
        private String codigo;
        private BigDecimal preco;
        private BigDecimal preco_promocional;
        private String unidade;
        private String situacao;
    }
}

