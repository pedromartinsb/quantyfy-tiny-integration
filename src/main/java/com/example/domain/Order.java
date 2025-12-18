package com.example.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tiny_id", nullable = false, unique = true)
    private String tinyId;

    @Column(name = "numero")
    private String numero;

    @Column(name = "numero_ecommerce")
    private String numeroEcommerce;

    @Column(name = "data_pedido")
    private LocalDate dataPedido;

    @Column(name = "data_faturamento")
    private LocalDate dataFaturamento;

    @Column(name = "data_entrega")
    private LocalDate dataEntrega;

    @Column(name = "nome_cliente")
    private String nomeCliente;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "total_produtos", precision = 19, scale = 4)
    private BigDecimal totalProdutos;

    @Column(name = "total_pedido", precision = 19, scale = 4)
    private BigDecimal totalPedido;

    @Column(name = "numero_ordem_compra")
    private String numeroOrdemCompra;

    @Column(name = "forma_envio")
    private String formaEnvio;

    @Column(name = "situacao")
    private String situacao;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> itens = new ArrayList<>();

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

