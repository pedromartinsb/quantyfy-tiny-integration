package com.example.service;

import com.example.client.TinyApiClient;
import com.example.domain.Product;
import com.example.repository.ProductRepository;
import com.example.dto.TinyProductDetailResponse;
import com.example.exception.TinyRateLimitException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDetailIngestionService {

    private final TinyApiClient client;
    private final ProductRepository repository;

    public void enrichProducts() {

        List<Product> products = repository.findAll();

        for (Product product : products) {
            try {
                this.enrich(product);

                // ⏱️ Delay seguro para Tiny
                Thread.sleep(800);

            } catch (TinyRateLimitException e) {
                log.error("⛔ Rate limit Tiny atingido. Abortando enriquecimento.");
                break;

            } catch (Exception e) {
                log.error("Erro ao enriquecer produto {}: {}", product.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    void enrich(Product product) {

        TinyProductDetailResponse response = client
                .getProductDetail(Long.valueOf(product.getTinyId()))
                .block();

        TinyProductDetailResponse.Produto tiny =
                response.getRetorno().getProduto();

        product.setNome(tiny.getNome());
        product.setCodigo(tiny.getCodigo());
        product.setCategoria(tiny.getCategoria());
        product.setIdProdutoPai(tiny.getIdProdutoPai());
        product.setClasseProduto(tiny.getClasse_produto());
        product.setTipo(tiny.getTipo());
        product.setTipoVariacao(tiny.getTipoVariacao());
        product.setPreco(tiny.getPreco());
        product.setPrecoCusto(tiny.getPreco_custo());
        product.setPrecoCustoMedio(tiny.getPreco_custo_medio());
        product.setPrecoPromocional(tiny.getPreco_promocional());
        product.setUnidade(tiny.getUnidade());
        product.setSituacao(tiny.getSituacao());

        repository.save(product);

        log.debug("Produto enriquecido: {}", product.getTinyId());
    }
}
