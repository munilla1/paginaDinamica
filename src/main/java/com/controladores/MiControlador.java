package com.controladores;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.model.Usuario;
import com.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class MiControlador {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
        	System.out.println("Guardando usuario: " + usuario);
            usuarioService.guardarUsuario(usuario);
            return "datosGuardados"; // ✅ Redirige después de guardar
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "registro-login";
        }
    }

    @PostMapping("/acceso")
    public String validarUsuario(@RequestParam String username, 
                                 @RequestParam String contrasenaIngresada, 
                                 HttpSession session, 
                                 Model model) {

        try {
            System.out.println("🔹 Intentando autenticación para: " + username);

            Usuario usuario = usuarioService.obtenerPorUsername(username);

            if (usuario == null || !passwordEncoder.matches(contrasenaIngresada, usuario.getPassword())) {
                throw new Exception("❌ Correo o contraseña incorrectos.");
            }

            System.out.println("✅ Usuario autenticado correctamente: " + usuario.getUsername());

            session.setAttribute("usuario", usuario); // Guarda el usuario en la sesión manualmente

            model.addAttribute("usuario", usuario);

            return "redirect:/accesoCorrecto"; // La vista de acceso correcto, que debe estar configurada en tu proyecto

        } catch (Exception e) {
            
            System.out.println("❌ Error de autenticación: " + e.getMessage());
            
            model.addAttribute("error", e.getMessage());

            return "registro-login"; // La vista de login, que debe estar configurada en tu proyecto
        }
    }


    @PostMapping("/eliminar")
    public String eliminarUsuario(@RequestParam String username, 
                                  @RequestParam String contrasenaIngresada, 
                                  Model model, 
                                  HttpSession session) {
        try {
            System.out.println("🔹 Intentando eliminar usuario con correo: " + username);

            // Buscar usuario en la base de datos
            Usuario usuario = usuarioService.obtenerPorUsername(username);


            // Validar la contraseña encriptada
            if (!passwordEncoder.matches(contrasenaIngresada, usuario.getPassword())) {
                throw new BadCredentialsException("❌ Correo o contraseña incorrectos.");
            }

            // Eliminar el usuario
            usuarioService.eliminarUsuario(usuario.getUsername());

            System.out.println("✅ Usuario eliminado correctamente: " + username);

            return "redirect:/usuarioEliminado"; // ✅ Redirige si la eliminación es exitosa

        } catch (UsernameNotFoundException | BadCredentialsException e) {
            System.out.println("❌ Error al eliminar usuario: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "DarDeBaja"; // ✅ Devuelve la vista con el mensaje de error
        } catch (Exception e) {
            System.out.println("⚠️ Error inesperado: " + e.getMessage());
            model.addAttribute("error", "⚠️ Ha ocurrido un error. Intenta de nuevo.");
            return "DarDeBaja"; // ✅ Muestra mensaje de error genérico
        }
    }

}



