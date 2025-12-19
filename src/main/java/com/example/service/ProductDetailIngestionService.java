package com.example.service;

import com.example.client.TinyApiClient;
import com.example.domain.Product;
import com.example.dto.TinyProductDetailResponse;
import com.example.dto.TinyProductStockResponse;
import com.example.exception.TinyRateLimitException;
import com.example.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDetailIngestionService {

    private final TinyApiClient client;
    private final ProductRepository repository;

    public void enrichProductsDetails() {

        List<Product> products = getProducts();

        for (Product product : products) {
            try {
                enrich(product);
                Thread.sleep(800);

            } catch (TinyRateLimitException e) {
                getRateLimitTinyErrorMessage();
                break;

            } catch (Exception e) {
                log.error("Erro ao enriquecer detalhes do produto {}: {}", product.getId(), e.getMessage());
            }
        }
    }

    public void enrichProductsStocks() {

        List<Product> products = getProducts();

        for (Product product : products) {
            try {
                enrichStock(product);
                Thread.sleep(800);

            } catch (TinyRateLimitException e) {
                getRateLimitTinyErrorMessage();
                break;

            } catch (Exception e) {
                log.error("Erro ao enriquecer estoque do produto {}: {}", product.getId(), e.getMessage());
            }
        }
    }

    private static void getRateLimitTinyErrorMessage() {
        log.error("⛔ Rate limit Tiny atingido. Abortando enriquecimento.");
    }

    @NotNull
    private List<Product> getProducts() {
        return repository.findAll()
                .stream()
                .filter(product -> product.getSituacao().equals("A"))
                .toList();
    }

    @Transactional
    private void enrich(Product product) {

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

    @Transactional
    private void enrichStock(Product product) {

        TinyProductStockResponse response = client
                .getProductStock(product.getTinyId())
                .block();

        if (response == null ||
                response.getRetorno() == null ||
                response.getRetorno().getProduto() == null) {
            log.warn("Estoque não retornado para produto {}", product.getTinyId());
            return;
        }

        TinyProductStockResponse.Produto tiny = response.getRetorno().getProduto();

        product.setEstoque(tiny.getSaldo());
        product.setEstoqueReservado(tiny.getSaldoReservado());

        repository.save(product);

        log.debug(
                "Estoque atualizado | product={} stock={} reserved={}",
                product.getId(),
                tiny.getSaldo(),
                tiny.getSaldoReservado()
        );
    }

}
