package com.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.model.PaymentEntity;
import com.model.Producto;
import com.model.Usuario;
import com.repository.PaymentRepository;
import com.service.CustomUserDetails;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final PaymentRepository paymentRepository;

    public PerfilController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping
    public String mostrarPerfil(Authentication authentication, Model model) {
        Usuario usuario = ((CustomUserDetails) authentication.getPrincipal()).getUsuario();

        List<Producto> productosComprados = paymentRepository.findByUsuario(usuario).stream()
            .map(PaymentEntity::getProducto)
            .collect(Collectors.toList());

        model.addAttribute("productosComprados", productosComprados);
        return "perfil"; // 🔹 Renderiza perfil.html con los datos cargados
    }
}

