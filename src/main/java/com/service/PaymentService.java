package com.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.model.PaymentEntity;
import com.model.PaymentRequest;
import com.model.PaymentResponse;
import com.model.Usuario;
import com.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    
    private final Map<Integer, BigDecimal> preciosProductos = Map.of(
        1, new BigDecimal("12.00"),
        2, new BigDecimal("14.00"),
        3, new BigDecimal("16.00")
    );

    @Value("${stripe.secret.key}")
    private String secretKey;
    
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("usd", "eur");

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse processPayment(PaymentRequest request) throws StripeException {
        System.out.println("📥 Procesando pago para producto ID: " + request.productoId() + ", email: " + request.stripeEmail());

        String currency = request.currency().toLowerCase();
        if (!SUPPORTED_CURRENCIES.contains(currency)) {
            throw new IllegalArgumentException("Moneda no soportada: " + currency);
        }

        // 🔹 Validación del producto
        if (!preciosProductos.containsKey(request.productoId())) {
            throw new IllegalArgumentException("Producto no válido.");
        }

        // 🔹 Obtener el precio fijo del backend
        BigDecimal precioFijado = preciosProductos.get(request.productoId());

        // 🔹 Depuración: Verificar `paymentMethodId` antes de continuar
        System.out.println("📥 Procesando pago para producto ID: " + request.productoId());
        System.out.println("🔹 Email recibido: " + request.stripeEmail());
        System.out.println("🔹 PaymentMethodId recibido: " + request.paymentMethodId());

        // 🔹 Crear PaymentIntent en Stripe con el monto en centavos
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(precioFijado.multiply(BigDecimal.valueOf(100)).longValue()) // Convertir a centavos
            .setCurrency(currency)
            .setPaymentMethod(request.paymentMethodId()) // ✅ Asegurar que se usa `paymentMethodId`
            .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC)
            .setConfirm(true)
            .setReturnUrl("http://localhost:8080/result")
            .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // 🔹 Depuración: Verifica el estado del PaymentIntent antes de continuar
        System.out.println("🔹 Estado del PaymentIntent: " + paymentIntent.getStatus());

        // 🔹 Validar que el pago se haya confirmado correctamente antes de continuar
        if (!paymentIntent.getStatus().equals("succeeded")) {
            throw new RuntimeException("El pago no fue confirmado correctamente.");
        }

        // 🔹 Obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Usuario usuario = userDetails.getUsuario();

        // 🔹 Guardar pago en la base de datos
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setProductoId(request.productoId());
        paymentEntity.setCurrency(request.currency());
        paymentEntity.setDescription(request.description());
        paymentEntity.setStatus(paymentIntent.getStatus());
        paymentEntity.setStripeEmail(request.stripeEmail());
        paymentEntity.setStripeToken(request.paymentMethodId());
        paymentEntity.setPaymentIntentId(paymentIntent.getId());
        paymentEntity.setCreatedAt(LocalDateTime.now());
        paymentEntity.setUsuario(usuario);

        paymentRepository.save(paymentEntity);

        // 🔹 Retornar respuesta con detalles del pago
        return new PaymentResponse(
            paymentIntent.getId(),
            Optional.ofNullable(paymentIntent.getAmount()).orElse(0L),
            request.currency(),
            paymentIntent.getStatus()
        );
    }
}

