package com.controladores;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.model.PaymentRequest;
import com.model.PaymentResponse;
import com.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    private final PaymentService paymentService;
    
    private final Map<Integer, BigDecimal> preciosProductos = Map.of(
            1, new BigDecimal("12.00"),
            2, new BigDecimal("14.00"),
            3, new BigDecimal("16.00")
        );

    public CheckoutController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/checkout")
    public String mostrarCheckout(@RequestParam("productoId") int productoId, Model model) {
        BigDecimal precio = preciosProductos.get(productoId);
        if (precio == null) return "error";

        model.addAttribute("productoId", productoId);
        model.addAttribute("precio", precio);
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currencies", List.of(Currency.getInstance("USD"), Currency.getInstance("EUR")));
        return "checkout";
    }
    
    @PostMapping(value = "/checkout", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> procesarPago(@RequestBody Map<String, Object> requestBody, HttpSession session) {
        System.out.println("📥 Datos recibidos en el backend: " + requestBody);

        int productoId = Integer.parseInt(requestBody.get("productoId").toString());
        String description = requestBody.get("description").toString();
        String currency = requestBody.get("currency").toString();
        String stripeEmail = requestBody.get("email") != null ? requestBody.get("email").toString() : "";
        String paymentMethodId = requestBody.get("paymentMethodId") != null ? requestBody.get("paymentMethodId").toString() : "";

        System.out.println("📥 Producto ID recibido: " + productoId);
        System.out.println("🔹 Email recibido: " + stripeEmail);
        System.out.println("🔹 PaymentMethodId recibido: " + paymentMethodId);


        // 🔹 Validación del método de pago
        if (paymentMethodId == null || paymentMethodId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("success", false, "message", "Error: Falta el método de pago."));
        }

        // 🔹 Procesar el pago con PaymentService
        PaymentRequest request = new PaymentRequest(productoId, description, currency, stripeEmail, paymentMethodId);
        try {
            PaymentResponse response = paymentService.processPayment(request);
            session.setAttribute("paymentIntentId", response.paymentIntentId());
            session.setAttribute("productoId", request.productoId());
            return ResponseEntity.ok(Map.of("success", true, "paymentId", response.paymentIntentId()));
        } catch (StripeException e) {
            System.err.println("❌ Error en Stripe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error en el pago: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Ocurrió un error inesperado."));
        }

    }
    
    @GetMapping("/result")
    public String mostrarResultado(@RequestParam(required = false) String paymentId, HttpSession session, Model model) {
        Integer productoId = (Integer) session.getAttribute("productoId");

        model.addAttribute("paymentIntentId", paymentId);
        model.addAttribute("productoId", productoId); // ✅ necesario para que se muestre el botón de descarga

        return "result";
    }
}

