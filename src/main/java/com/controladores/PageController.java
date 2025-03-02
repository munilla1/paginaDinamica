package com.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.model.Usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {
    
    @GetMapping("/perfil")
    public String perfilPage(HttpServletRequest request, Model model) {
        String sesionVerificada = verificarSesion(request);  // Verificamos si la sesión es válida

        // Si el usuario está autenticado, pasamos el objeto usuario al modelo
        if (sesionVerificada != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuario");  // Obtener usuario de la sesión
                model.addAttribute("usuario", usuario);  // Pasar el usuario al modelo
            }
            return "perfil";  // Redirige a la vista de login o cualquier otra vista
        }
        return "perfil";  // Si no está autenticado, muestra el login
    }
    
    @GetMapping("/")
    public String indexPage(HttpServletRequest request, Model model) {
        String sesionVerificada = verificarSesion(request);  // Verificamos si la sesión es válida

        // Si el usuario está autenticado, pasamos el objeto usuario al modelo
        if (sesionVerificada != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuario");  // Obtener usuario de la sesión
                model.addAttribute("usuario", usuario);  // Pasar el usuario al modelo
            }
            return "index";  // Redirige a la vista de login o cualquier otra vista
        }
        return "index";  // Si no está autenticado, muestra el login
    }
    
    
    @GetMapping("/accesoCorrecto")
    public String accesoCorrectoPage(HttpServletRequest request, Model model) {
        String sesionVerificada = verificarSesion(request);  // Verificamos si la sesión es válida

        // Si el usuario está autenticado, pasamos el objeto usuario al modelo
        if (sesionVerificada != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuario");  // Obtener usuario de la sesión
                model.addAttribute("usuario", usuario);  // Pasar el usuario al modelo
            }
            return "accesoCorrecto";  // Redirige a la vista de login o cualquier otra vista
        }
        return "accesoCorrecto";  // Si no está autenticado, muestra el login
    }


    @GetMapping("/registro-login")
    public String registroLoginPage(HttpServletRequest request, Model model) {
        String sesionVerificada = verificarSesion(request);  // Verificamos si la sesión es válida

        // Si el usuario está autenticado, pasamos el objeto usuario al modelo
        if (sesionVerificada != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuario");  // Obtener usuario de la sesión
                model.addAttribute("usuario", usuario);  // Pasar el usuario al modelo
            }
            return "registro-login";  // Redirige a la vista de login o cualquier otra vista
        }
        return "registro-login";  // Si no está autenticado, muestra el login
    }
    
    @GetMapping("/DarDeBaja")
    public String eliminarUsuPage(HttpServletRequest request, Model model) {
        String sesionVerificada = verificarSesion(request);  // Verificamos si la sesión es válida

        // Si el usuario está autenticado, pasamos el objeto usuario al modelo
        if (sesionVerificada != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuario");  // Obtener usuario de la sesión
                model.addAttribute("usuario", usuario);  // Pasar el usuario al modelo
            }
            return "DarDeBaja";  // Redirige a la vista de login o cualquier otra vista
        }
        return "DarDeBaja";  // Si no está autenticado, muestra el login
    }


    private String verificarSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);  // false para no crear una nueva sesión si no existe
        if (session == null || session.getAttribute("usuario") == null) {
            return "redirect:/login";  // Redirigir al login si no hay sesión
        }
        return "pagina";  // Redirigir a la página correspondiente si el usuario está autenticado
    }
    
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Obtener sesión si existe
        
        if (session == null || session.getAttribute("usuario") == null) {
            return "redirect:/"; // Redirige al login si la sesión ha expirado
        }

        session.invalidate();
        return "redirect:/";  // Redirige a la página de inicio (login)
    }

    
    @GetMapping("/pagPrincipalJuego")
    public String juegoPage(HttpServletRequest request, Model model) {
        String sesionVerificada = verificarSesion(request);  // Verificamos si la sesión es válida

        // Si el usuario está autenticado, pasamos el objeto usuario al modelo
        if (sesionVerificada != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Usuario usuario = (Usuario) session.getAttribute("usuario");  // Obtener usuario de la sesión
                model.addAttribute("usuario", usuario);  // Pasar el usuario al modelo
            }
            return "pagPrincipalJuego";  // Redirige a la vista de login o cualquier otra vista
        }
        return "pagPrincipalJuego";  // Si no está autenticado, muestra el login
    }

}
