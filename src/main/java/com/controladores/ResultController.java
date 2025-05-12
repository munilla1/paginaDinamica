package com.controladores;

import java.util.Comparator;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.model.PaymentEntity;
import com.repository.PaymentRepository;

@Controller
public class ResultController {

    private final PaymentRepository paymentRepository;

    public ResultController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/result")
    public String showResult(@RequestParam(value = "paymentId", required = false) String paymentIntentId,
                             Model model) {
        if (paymentIntentId != null) {
            Optional<PaymentEntity> optionalPayment = paymentRepository.findByPaymentIntentId(paymentIntentId);
            optionalPayment.ifPresent(payment -> model.addAttribute("payment", payment));
        }
        return "result";
    }

}

