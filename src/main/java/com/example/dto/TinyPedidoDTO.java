package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TinyPedidoDTO {

    private String id;
    private String numero;

    @JsonProperty("numero_ecommerce")
    private String numeroEcommerce;

    @JsonProperty("data_pedido")
    private String dataPedido;

    @JsonProperty("data_faturamento")
    private String dataFaturamento;

    @JsonProperty("data_entrega")
    private String dataEntrega;

    private String situacao;

    @JsonProperty("forma_envio")
    private String formaEnvio;

    @JsonProperty("total_produtos")
    private String totalProdutos;

    @JsonProperty("total_pedido")
    private String totalPedido;

    @JsonProperty("valor_frete")
    private String valorFrete;

    private String obs;

    private List<TinyItemWrapperDTO> itens = new ArrayList<>();
}

