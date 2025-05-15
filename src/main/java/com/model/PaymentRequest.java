package com.model;

import java.math.BigDecimal;

public record PaymentRequest(
		int productoId,
        String description,
        String currency,
        String stripeEmail,
        String paymentMethodId
) {
}
