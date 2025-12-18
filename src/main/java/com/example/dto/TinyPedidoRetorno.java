package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TinyPedidoRetorno {

    private String status;
    private String status_processamento;

    @JsonProperty("pedido")
    private TinyPedidoDTO pedido;
}

