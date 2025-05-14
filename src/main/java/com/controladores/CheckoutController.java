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

    public CheckoutController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/checkout")
    public String showCheckoutForm(Model model) {
        model.addAttribute("stripePublicKey", stripePublicKey);
        List<Currency> allowedCurrencies = List.of(
                Currency.getInstance("USD"),
                Currency.getInstance("EUR")
            );
        model.addAttribute("currencies", allowedCurrencies);
        return "checkout";
    }
    
    @PostMapping(value = "/checkout", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> processPaymentJson(@RequestBody Map<String, Object> payload) {
        try {
            String description = (String) payload.get("description");
            BigDecimal amount = new BigDecimal((String) payload.get("amount"));
            String currency = (String) payload.get("currency");
            String email = (String) payload.get("email");
            String paymentMethodId = (String) payload.get("paymentMethodId");

            PaymentRequest request = new PaymentRequest(description, amount, currency, email, paymentMethodId);
            PaymentResponse response = paymentService.processPayment(request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("paymentId", response.paymentIntentId());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/view-pdf")
    public ResponseEntity<Resource> viewPdf(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);

        if (!isLoggedIn) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String paymentIntentId = (String) session.getAttribute("paymentIntentId");
        if (paymentIntentId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            if (!"succeeded".equals(paymentIntent.getStatus())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Ruta del archivo (PDF o imagen)
            Path filePath = Paths.get("src/main/resources/static/pdfs/infografiaSpring.jpg");
            // Path filePath = Paths.get("src/main/resources/static/pdfs/infografiaSpring.pdf"); // otro ejemplo

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Detectar tipo de archivo (MIME)
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // por defecto si no se detecta
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);

        } catch (StripeException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

