package com.example.client;

import com.example.dto.TinyProductDetailResponse;
import com.example.dto.TinyProductResponse;
import com.example.dto.TinyProductStockResponse;
import com.example.dto.TinyProperties;
import com.example.exception.TinyApiException;
import com.example.exception.TinyRateLimitException;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class TinyApiClient {

    private static final String TOKEN_PARAM = "token";
    private static final String FORMATO_PARAM = "formato";

    private final WebClient webClient;
    private final TinyProperties props;
    private final ObjectMapper objectMapper;

    @RateLimiter(name = "tinyApi")
    @Retry(name = "tinyApi")
    public Mono<TinyProductResponse> searchProducts(int page) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(TOKEN_PARAM, props.getToken());
        formData.add(FORMATO_PARAM, "JSON");
        formData.add("pagina", String.valueOf(page));

        return webClient.post()
                .uri("/produtos.pesquisa.php")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> log.info("Resposta Tiny RAW: {}", body))
                .map(body -> new ObjectMapper().readValue(body, TinyProductResponse.class));
    }

    public Mono<TinyProductDetailResponse> getProductDetail(Long productId) {

        return webClient.post()
                .uri("/produto.obter.php")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(TOKEN_PARAM, props.getToken())
                        .with("id", String.valueOf(productId))
                        .with(FORMATO_PARAM, "JSON"))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new TinyApiException(
                                                "Erro HTTP Tiny: " + body
                                        )
                                ))
                )
                .bodyToMono(String.class)
                .flatMap(this::parseTinyResponse);
    }

    public Mono<TinyProductStockResponse> getProductStock(String productId) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/produto.obter.estoque.php")
                        .queryParam(TOKEN_PARAM, props.getToken())
                        .queryParam("id", productId)
                        .queryParam(FORMATO_PARAM, "json")
                        .build()
                )
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    try {
                                        ObjectMapper mapper = new ObjectMapper();
                                        return Mono.just(
                                                mapper.readValue(body, TinyProductStockResponse.class)
                                        );
                                    } catch (Exception e) {
                                        return Mono.error(
                                                new TinyApiException(
                                                        "Erro ao parsear estoque Tiny: " + body, e
                                                )
                                        );
                                    }
                                })
                );
    }

    private Mono<TinyProductDetailResponse> parseTinyResponse(String body) {

        try {
            TinyProductDetailResponse response =
                    objectMapper.readValue(body, TinyProductDetailResponse.class);

            TinyProductDetailResponse.Retorno retorno = response.getRetorno();

            if (retorno.getCodigo_erro() != null && retorno.getCodigo_erro() == 6) {
                return Mono.error(new TinyRateLimitException(
                        "Tiny rate limit: " + body
                ));
            }

            if (!"OK".equalsIgnoreCase(retorno.getStatus())) {
                return Mono.error(new TinyApiException(
                        "Erro funcional Tiny: " + body
                ));
            }

            return Mono.just(response);

        } catch (Exception e) {
            return Mono.error(new TinyApiException(
                    "Erro ao parsear resposta Tiny: " + body, e
            ));
        }
    }
}
