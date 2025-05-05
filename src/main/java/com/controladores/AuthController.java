package com.controladores;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.model.ERole;
import com.model.Role;
import com.model.Usuario;
import com.repository.RoleRepository;
import com.service.CustomUserDetails;
import com.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/registro-login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
    	    CustomUserDetails usuario = (CustomUserDetails) authentication.getPrincipal();
    	    System.out.println("Usuario autenticado: " + usuario.getUsername());
    	}

    	
        if (error != null) {
            model.addAttribute("errorDeAcceso", "Nombre de usuario o contraseña incorrectos.");
        }
        return "registro-login";
    }
    
    @GetMapping("/accesoCorrecto")
    public String accesoCorrecto(@AuthenticationPrincipal Usuario usuario, Model model) {
        if (usuario == null) {
            return "registro-login";
        }
        model.addAttribute("usuario", usuario);
        return "accesoCorrecto";
    }


    @PostMapping("/guardar")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String correo,
                               Model model) {
        if (usuarioService.existsByUsername(username)) {
            model.addAttribute("error", "El nombre de usuario ya está en uso.");
            return "registro-login";
        }
        
        Role rolUsuario = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Rol USER no encontrado"));


        Usuario usuario = Usuario.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .correo(correo)
                .roles(Set.of(rolUsuario))
                .build();

        usuarioService.save(usuario);
        model.addAttribute("mensajeRegistro", "Usuario registrado exitosamente.");
        return "registro-login";
    }

    @GetMapping("/perfil")
    public String userProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioService.findByUsername(userDetails.getUsername());
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping("/DarDeBaja")
    public String deleteUser(@RequestParam Long id) {
        usuarioService.delete(id);
        return "redirect:/";
    }
}


