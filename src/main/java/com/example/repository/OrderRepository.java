package com.example.repository;

import com.example.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByTinyId(String tinyId);

    @Query("""
        select o
        from Order o
        left join fetch o.itens
        where o.tinyId = :id
    """)
    Optional<Order> findByIdWithItens(@Param("id") String id);

    @Query("""
        select o
        from Order o
        where dataEntrega is null
        and dataPedido between :dataInicial and :dataFinal
    """)
    List<Order> findByDataPedidoBetween(LocalDate dataInicial, LocalDate dataFinal);

//    List<Order> findAllByDataPedidoBetween(LocalDate dataInicial, LocalDate dataFinal);

    @Query("""
        SELECT o
        FROM Order o
        JOIN FETCH o.itens
        WHERE dataPedido between :dataInicial and :dataFinal
    """)
    List<Order> findAllByDataPedidoBetween(LocalDate dataInicial, LocalDate dataFinal);
}

