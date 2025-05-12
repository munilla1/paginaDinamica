package com.controladores;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.model.PaymentRequest;
import com.model.PaymentResponse;
import com.service.PaymentService;

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
        model.addAttribute("currencies", Currency.getAvailableCurrencies());
        return "checkout";
    }

	/*
	 * @PostMapping("/checkout") public String processPayment(@RequestParam String
	 * description,
	 * 
	 * @RequestParam BigDecimal amount,
	 * 
	 * @RequestParam String currency,
	 * 
	 * @RequestParam String email,
	 * 
	 * @RequestParam String stripeToken, // Aquí recibirás el token de Stripe
	 * RedirectAttributes redirectAttributes) {
	 * 
	 * System.out.println("Token de Stripe: " + stripeToken); try { // Crea una
	 * solicitud para procesar el pago PaymentRequest request = new
	 * PaymentRequest(description, amount, currency, email, stripeToken);
	 * 
	 * // Procesa el pago con la API de Stripe PaymentResponse response =
	 * paymentService.processPayment(request);
	 * 
	 * // Agrega el paymentId al modelo para redirigir al resultado
	 * redirectAttributes.addAttribute("paymentId", response.paymentIntentId());
	 * redirectAttributes.addFlashAttribute("success", "Pago exitoso!");
	 * 
	 * return "redirect:/result"; } catch (Exception e) {
	 * redirectAttributes.addFlashAttribute("error", "Error en el pago: " +
	 * e.getMessage()); return "redirect:/result"; } }
	 */
    
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

}

