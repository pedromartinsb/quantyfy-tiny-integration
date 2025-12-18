package com.example.client;

import com.example.dto.TinyOrderSearchResponse;
import com.example.dto.TinyPedidoDetalheResponse;
import com.example.dto.TinyProperties;
import com.example.exception.TinyApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class TinyOrderClient {

    private final WebClient webClient;
    private final TinyProperties props;

    public TinyOrderSearchResponse searchOrders(
            int page,
            String dataInicial,
            String dataFinal
    ) {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/pedidos.pesquisa.php")
                        .queryParam("pagina", page)
                        .queryParam("limite", 100)
                        .queryParam("sort", "ASC")
                        .build()
                )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData("token", props.getToken())
                        .with("dataInicial", dataInicial)
                        .with("dataFinal", dataFinal)
                        .with("formato", "JSON"))
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    try {
                                        ObjectMapper mapper = new ObjectMapper();
                                        return Mono.just(
                                                mapper.readValue(body, TinyOrderSearchResponse.class)
                                        );
                                    } catch (Exception e) {
                                        throw new TinyApiException(
                                                "Erro ao parsear pedidos Tiny: " + body, e
                                        );
                                    }
                                })
                )
                .block();
    }

    public TinyPedidoDetalheResponse obterPedido(String tinyId, String token) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/pedido.obter.php")
                        .queryParam("id", tinyId)
                        .queryParam("token", token)
                        .queryParam("formato", "json")
                        .build()
                )
                .exchangeToMono(response -> {

                    MediaType contentType = response.headers()
                            .contentType()
                            .orElse(MediaType.TEXT_HTML);

                    return response.bodyToMono(String.class)
                            .flatMap(body -> {

                                // 🔴 Tiny pode devolver HTML ou JSON com erro
                                if (!body.trim().startsWith("{")) {
                                    return Mono.error(new TinyApiException(
                                            "Resposta não JSON da Tiny: " + body
                                    ));
                                }

                                try {
                                    ObjectMapper mapper = new ObjectMapper();
                                    TinyPedidoDetalheResponse parsed =
                                            mapper.readValue(body, TinyPedidoDetalheResponse.class);
                                    return Mono.just(parsed);

                                } catch (Exception e) {
                                    return Mono.error(new TinyApiException(
                                            "Erro ao parsear resposta Tiny: " + body, e
                                    ));
                                }
                            });
                })
                .block();
    }

}

