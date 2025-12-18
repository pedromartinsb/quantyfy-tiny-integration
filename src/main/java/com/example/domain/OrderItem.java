package com.example.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

//    @Column(name = "product_codigo", length = 50)
//    private String productCodigo;
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_codigo", referencedColumnName = "codigo")
    private Product product;

    @Column(name = "unidade", length = 10)
    private String unidade;

    @Column(name = "quantidade", precision = 19, scale = 4)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", precision = 19, scale = 4)
    private BigDecimal valorUnitario;

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

