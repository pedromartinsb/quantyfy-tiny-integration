package com.example.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TinyProductDetailResponse {

    private Retorno retorno;

    @Data
    public static class Retorno {
        private String status;
        private Integer codigo_erro;
        private List<Erro> erros;
        private Produto produto;
    }

    @Data
    public static class Erro {
        private String erro;
    }

    @Data
    public static class Produto {
        private String id;
        private String idProdutoPai;
        private String nome;
        private String codigo;
        private String categoria;
        private String classe_produto;
        private BigDecimal preco;
        private BigDecimal preco_promocional;
        private BigDecimal preco_custo;
        private BigDecimal preco_custo_medio;
        private String unidade;
        private String situacao;
        private String tipo;
        private String tipoVariacao;
    }
}
