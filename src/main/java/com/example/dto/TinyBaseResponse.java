package com.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class TinyBaseResponse {

    private Retorno retorno;

    @Data
    public static class Retorno {
        private String status;
        private Integer status_processamento;
        private Integer codigo_erro;
        private List<Erro> erros;
    }

    @Data
    public static class Erro {
        private String erro;
    }
}

