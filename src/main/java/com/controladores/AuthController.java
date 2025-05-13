package com.controladores;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.model.ERole;
import com.model.PaymentRequest;
import com.model.Role;
import com.model.Usuario;
import com.repository.RoleRepository;
import com.service.CustomUserDetails;
import com.service.PaymentService;
import com.service.UsuarioService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/registro-login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @AuthenticationPrincipal CustomUserDetails usuario,
                                Model model) {

        // Agregar mensaje de error si existe
        if (error != null) {
            model.addAttribute("errorDeAcceso", "Nombre de usuario o contraseña incorrectos.");
        }

        // Verificar si el usuario está autenticado y agregarlo al modelo
        if (usuario != null) {
            System.out.println("Usuario autenticado: " + usuario.getUsername());
            model.addAttribute("usuario", usuario);
        }

        return "registro-login";
    }

    
    @GetMapping("/accesoCorrecto")
    public String accesoCorrecto(@AuthenticationPrincipal CustomUserDetails usuario, HttpSession session, Model model) {
        if (usuario == null) {
            return "redirect:/registro-login";
        }
        session.setAttribute("usuario", usuario.getUsuario());
        model.addAttribute("usuario", usuario);
        return "accesoCorrecto";
    }
    
    @GetMapping("/pagPrincipalJuego")
    public String vistaPagPrincipal(@AuthenticationPrincipal CustomUserDetails usuario, HttpSession session, Model model) {
        if (usuario == null) {
            return "redirect:/registro-login";
        }
        session.setAttribute("usuario", usuario.getUsuario());
        model.addAttribute("usuario", usuario);
        return "pagPrincipalJuego";
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
    
    @PostMapping("/modificar")
    public String actualizarPerfil(@RequestParam String username,
                                   @RequestParam String correo,
                                   @RequestParam String password,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {

        Usuario usuario = usuarioService.findByUsername(userDetails.getUsername());

        usuario.setUsername(username);
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(password)); // ⚠️ Codifica la nueva contraseña

        usuarioService.save(usuario); // Esto hace un update si el usuario ya existe

        model.addAttribute("usuario", usuario);
        model.addAttribute("mensaje", "Perfil actualizado correctamente.");

        return "perfil"; // O vuelve a cargar el formulario si prefieres
    }

    @GetMapping("/perfil")
    public String userProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioService.findByUsername(userDetails.getUsername());
        model.addAttribute("usuario", usuario);
        return "perfil";
    }
    
    @GetMapping("/infografias")
    public String userInfografias(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioService.findByUsername(userDetails.getUsername());
        model.addAttribute("usuario", usuario);
        return "infografias";
    }

    @PostMapping("/eliminar")
    public String deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String username,
                             @RequestParam String contrasenaIngresada,
                             Model model,
                             HttpServletRequest request,
                             HttpServletResponse response) {

        // Verificación de identidad
        if (!userDetails.getUsername().equals(username)) {
            model.addAttribute("error", "No puedes eliminar otro usuario que no seas tú.");
            return "DarDeBaja";
        }

        // Obtener usuario
        Usuario usuario = usuarioService.findByUsername(username);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "DarDeBaja";
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(contrasenaIngresada, usuario.getPassword())) {
            model.addAttribute("error", "Contraseña incorrecta.");
            return "DarDeBaja";
        }

        // Eliminar usuario
        usuarioService.delete(usuario);

        // Invalida sesión y contexto de seguridad
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Borra cookies de sesión (opcional pero recomendable)
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // Redirige al login o página principal
        return "redirect:/";
    }
    
    @GetMapping("/DarDeBaja")
    public String vistaDeleteUser(@AuthenticationPrincipal CustomUserDetails usuario, HttpSession session, Model model) {
        return "DarDeBaja";
    }
}


