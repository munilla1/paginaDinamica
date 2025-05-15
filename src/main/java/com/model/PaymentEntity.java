package com.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private String description;
    private String currency;
    private String status;
    private String stripeEmail;
    private String stripeToken;
    private String paymentIntentId;
    private LocalDateTime createdAt;
    private int productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // nombre de la columna FK en la tabla payments
    private Usuario usuario;

}
