package com.controladores;

import java.util.Comparator;
import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.model.PaymentEntity;
import com.repository.PaymentRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ResultController {

    private final PaymentRepository paymentRepository;

    public ResultController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/result")
    public String showResult(@RequestParam(value = "paymentId", required = false) String paymentIntentId,
                             Model model, HttpSession session) {

        // Verificación de login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
        model.addAttribute("isLoggedIn", isLoggedIn);

        // Verificación de pago
        if (paymentIntentId != null) {
            Optional<PaymentEntity> optionalPayment = paymentRepository.findByPaymentIntentId(paymentIntentId);
            optionalPayment.ifPresent(payment -> {
                model.addAttribute("payment", payment);

                // También puedes guardar en sesión si quieres usarlo en /view-pdf
                session.setAttribute("paymentIntentId", paymentIntentId);
            });
        }

        return "result";
    }


}

