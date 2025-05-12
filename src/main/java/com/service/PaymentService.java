package com.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.model.PaymentEntity;
import com.model.PaymentRequest;
import com.model.PaymentResponse;
import com.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.secret.key}")
    private String secretKey;

    // Inicializa la clave secreta de Stripe después de construir el bean.
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Procesa un pago basado en la solicitud recibida.
    public PaymentResponse processPayment(PaymentRequest request) throws StripeException {
        System.out.println("📥 Procesando pago para: " + request.description() + ", email: " + request.stripeEmail());
        
        // Construye los parámetros para crear un PaymentIntent en Stripe
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(request.amount().multiply(BigDecimal.valueOf(100)).longValue()) // Convierte el monto a centavos
                .setCurrency(request.currency().toLowerCase()) // Asegúrate de que la moneda esté en minúsculas
                .setPaymentMethod(request.stripeToken()) // Usa el paymentMethodId que recibiste
                .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL) // Confirmación manual
                .setConfirm(true) // Confirmación automática
                .setReturnUrl("http://localhost:8080/result") // URL para redirigir después del pago
                .build();

        // Envía los parámetros a Stripe para crear un PaymentIntent
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Crea una nueva entidad de pago para guardar en la base de datos
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setAmount(request.amount());
        paymentEntity.setCurrency(request.currency());
        paymentEntity.setDescription(request.description());
        paymentEntity.setStatus(paymentIntent.getStatus());
        paymentEntity.setStripeEmail(request.stripeEmail());
        paymentEntity.setStripeToken(request.stripeToken()); // Guarda el paymentMethodId (anteriormente stripeToken)
        paymentEntity.setPaymentIntentId(paymentIntent.getId());
        paymentEntity.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(paymentEntity); // Guarda el pago en la base de datos

        // Devuelve una respuesta con los detalles del pago
        return new PaymentResponse(
                paymentIntent.getId(),
                paymentIntent.getAmount(),
                request.currency(),
                paymentIntent.getStatus()
        );
    }


}

