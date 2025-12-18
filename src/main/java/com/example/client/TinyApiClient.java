package com.example.client;

import com.example.dto.TinyProductDetailResponse;
import com.example.dto.TinyProductResponse;
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

    private final WebClient webClient;
    private final TinyProperties props;
    private final ObjectMapper objectMapper;

    @RateLimiter(name = "tinyApi")
    @Retry(name = "tinyApi")
    public Mono<TinyProductResponse> searchProducts(int page) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("token", props.getToken());
        formData.add("formato", "JSON");
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
                .body(BodyInserters.fromFormData("token", props.getToken())
                        .with("id", String.valueOf(productId))
                        .with("formato", "JSON"))
                .retrieve()
                // 🔥 Sempre capturar erro HTTP primeiro
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new TinyApiException(
                                                "Erro HTTP Tiny: " + body
                                        )
                                ))
                )
                // 🧠 Ler SEMPRE como String
                .bodyToMono(String.class)
                .flatMap(this::parseTinyResponse);
    }

    private Mono<TinyProductDetailResponse> parseTinyResponse(String body) {

        try {
            TinyProductDetailResponse response =
                    objectMapper.readValue(body, TinyProductDetailResponse.class);

            TinyProductDetailResponse.Retorno retorno = response.getRetorno();

            // ⛔ Rate limit Tiny
            if (retorno.getCodigo_erro() != null && retorno.getCodigo_erro() == 6) {
                return Mono.error(new TinyRateLimitException(
                        "Tiny rate limit: " + body
                ));
            }

            // ❌ Erros funcionais Tiny
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


//    @RateLimiter(name = "tinyApi")
//    @Retry(name = "tinyApi")
//    public Mono<TinyProductDetailResponse> getProductDetail(String productId) {
//
//        return webClient.post()
//                .uri("/produto.obter.php")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .accept(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromFormData("token", props.getToken())
//                        .with("id", productId)
//                        .with("formato", "JSON"))
//                .exchangeToMono(response ->
//                        response.bodyToMono(String.class)
//                                .flatMap(raw -> {
//
//                                    String body = raw.trim();
//
//                                    // 🔥 CASO 1 — HTML REAL (erro Tiny / proxy / bloqueio)
//                                    if (body.startsWith("<")) {
//                                        return Mono.error(
//                                                new TinyApiException(
//                                                        "Resposta HTML da Tiny: " + body
//                                                )
//                                        );
//                                    }
//
//                                    // 🔥 CASO 2 — NÃO é JSON válido
//                                    if (!body.startsWith("{")) {
//                                        return Mono.error(
//                                                new TinyApiException(
//                                                        "Resposta desconhecida da Tiny: " + body
//                                                )
//                                        );
//                                    }
//
//                                    try {
//                                        ObjectMapper mapper = new ObjectMapper();
//
//                                        // 1️⃣ Parse base (status, erro, codigo)
//                                        TinyBaseResponse base =
//                                                mapper.readValue(body, TinyBaseResponse.class);
//
//                                        // 2️⃣ Erro de negócio da Tiny
//                                        if (!"OK".equalsIgnoreCase(
//                                                base.getRetorno().getStatus())) {
//
//                                            String mensagemErro =
//                                                    base.getRetorno().getErros() != null
//                                                            ? base.getRetorno().getErros().stream()
//                                                            .map(TinyBaseResponse.Erro::getErro)
//                                                            .collect(Collectors.joining(" | "))
//                                                            : "Erro desconhecido da Tiny";
//
//                                            return Mono.error(
//                                                    new TinyApiException(
//                                                            "Erro Tiny (codigo "
//                                                                    + base.getRetorno().getCodigo_erro()
//                                                                    + "): " + mensagemErro
//                                                    )
//                                            );
//                                        }
//
//                                        // 3️⃣ Sucesso → parse final
//                                        TinyProductDetailResponse success =
//                                                mapper.readValue(body, TinyProductDetailResponse.class);
//
//                                        return Mono.just(success);
//
//                                    } catch (Exception e) {
//                                        return Mono.error(
//                                                new TinyApiException(
//                                                        "Erro ao parsear JSON da Tiny", e
//                                                )
//                                        );
//                                    }
//                                })
//                );
//    }
}
