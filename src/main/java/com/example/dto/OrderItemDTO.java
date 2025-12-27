package com.example.dto;

import com.example.domain.Order;

import java.math.BigDecimal;

public record OrderItemDTO(Order order,
                           BigDecimal quantidade) {
}
