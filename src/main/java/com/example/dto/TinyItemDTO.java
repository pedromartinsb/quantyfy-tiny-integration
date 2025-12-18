package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TinyItemDTO {

    @JsonProperty("id_produto")
    private String idProduto;

    private String codigo;
    private String descricao;
    private String unidade;
    private String quantidade;

    @JsonProperty("valor_unitario")
    private String valorUnitario;
}

