package com.example.repository;

import com.example.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}

