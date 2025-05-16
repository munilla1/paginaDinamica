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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                Model model, HttpServletRequest request) {

        // Agregar mensaje de error si existe
        if (error != null) {
            model.addAttribute("errorDeAcceso", "Nombre de usuario o contraseña incorrectos.");
        }

        // Verificar si el usuario está autenticado y agregarlo al modelo
        if (usuario != null) {
            System.out.println("Usuario autenticado: " + usuario.getUsername());
            model.addAttribute("usuario", usuario);
            model.addAttribute("accion", "login");
        }
        return "registro-login";
    }

    
    @GetMapping("/accesoCorrecto")
    public String accesoCorrecto(@AuthenticationPrincipal CustomUserDetails usuario, HttpSession session, Model model, HttpServletRequest request) {
        if (usuario == null) {
            return "redirect:/registro-login";
        }
        session.setAttribute("usuario", usuario.getUsuario());
        model.addAttribute("usuario", usuario);
        return "accesoCorrecto";
    }
    
    @GetMapping("/pagPrincipalJuego")
    public String vistaPagPrincipal(@AuthenticationPrincipal CustomUserDetails usuario, HttpSession session, Model model, HttpServletRequest request) {
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
                                   HttpServletRequest request, // 🔹 Necesario para la sesión
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioService.findByUsername(userDetails.getUsername());

        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "perfil";
        }

        usuario.setUsername(username);
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(password));

        usuarioService.save(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("accion", "modificar");
        
        session.invalidate();
        
        // 🔹 Agregar mensaje de éxito
        redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente. Por seguridad, se ha cerrado la sesión.");

        return "redirect:/registro-login";
    }

    
    @GetMapping("/infografias")
    public String userInfografias(Model model, HttpSession session, HttpServletRequest request) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login"; // 🔹 Evita que usuarios sin sesión accedan
        }
        model.addAttribute("usuario", usuario);
        return "infografias";
    }

    @PostMapping("/eliminar")
    public String deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String username,
                             @RequestParam String contrasenaIngresada,
                             Model model,
                             HttpSession session,
                             HttpServletRequest request,
                             HttpServletResponse response) {
    	
    	Usuario usuario = (Usuario) session.getAttribute("usuario");
    	
        // Verificación de identidad
        if (!userDetails.getUsername().equals(username)) {
            model.addAttribute("error", "No puedes eliminar otro usuario que no seas tú.");
            return "perfil";
        }

        // Obtener usuario
        usuarioService.findByUsername(username);
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado.");
            return "perfil";
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(contrasenaIngresada, usuario.getPassword())) {
            model.addAttribute("error", "Contraseña incorrecta.");
            return "perfil";
        }

        // Eliminar usuario
        usuarioService.delete(usuario);

        // Invalida sesión y contexto de seguridad
        SecurityContextHolder.clearContext();
        request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Borra cookies de sesión (opcional pero recomendable)
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // Redirige al login o página principal
        return "redirect:/registro-login";
    }
    
    @GetMapping("/")
    public String vistaUser(@AuthenticationPrincipal CustomUserDetails usuario, HttpServletRequest request, HttpSession session, Model model) {

        return "registro-login";
    }
}


