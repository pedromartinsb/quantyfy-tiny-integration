package com.example.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = "tiny_id")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tiny_id", nullable = false, unique = true)
    private String tinyId;

    @Column(name = "id_produto_pai")
    private String idProdutoPai;

    @Column(name = "classe_produto")
    private String classeProduto;

    private String tipo;

    @Column(name = "tipo_variacao")
    private String tipoVariacao;

    private String nome;
    private String codigo;
    private String categoria;
    private BigDecimal preco;
    private String situacao;
    private String unidade;

    @Column(name = "preco_promocional")
    private BigDecimal precoPromocional;

    @Column(name = "preco_custo")
    private BigDecimal precoCusto;

    @Column(name = "preco_custo_medio")
    private BigDecimal precoCustoMedio;

    private Integer stock;

    @Column(name = "stock_reserved")
    private Integer stockReserved;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
