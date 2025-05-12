package com.model;

import java.math.BigDecimal;

public record PaymentRequest(
        String description,
        BigDecimal amount,
        String currency,
        String stripeEmail,
        String stripeToken
) {
}
