package com.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public String guardarUsuario(@ModelAttribute Usuario usuario, Model model, HttpSession session) {
        try {
        	System.out.println("Guardando usuario: " + usuario);
            usuarioService.guardarUsuario(usuario);
            model.addAttribute("mensajeRegistro", "Tu usuario se registró correctamente.");
            return "registro-login"; // ✅ Redirige después de guardar
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
                throw new Exception("❌ Nombre o contraseña incorrectos.");
            }

            System.out.println("✅ Usuario autenticado correctamente: " + usuario.getUsername());

            session.setAttribute("usuario", usuario); // Guarda el usuario en la sesión manualmente

            model.addAttribute("usuario", usuario);

            return "redirect:/accesoCorrecto"; // La vista de acceso correcto, que debe estar configurada en tu proyecto

        } catch (Exception e) {
            
            System.out.println("❌ Error de autenticación: " + e.getMessage());
            
            model.addAttribute("errorDeAcceso", e.getMessage());

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
            usuarioService.eliminarUsuario(usuario.getUsername(), session);

            System.out.println("✅ Usuario eliminado correctamente: " + username);
            model.addAttribute("mensajeEliminacion", "El usuario ha sido eliminado correctamente.");

            return "DarDeBaja"; // ✅ Redirige si la eliminación es exitosa

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
    
    @PostMapping("/modificar")
    public String modificarUsuario(HttpSession session, 
                                   @RequestParam String username, 
                                   @RequestParam String correo, 
                                   @RequestParam String password, 
                                   Model model) {
        try {
            // Obtener el usuario actual desde la sesión
            Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
            if (usuarioAutenticado == null) {
                throw new Exception("Usuario no encontrado en la sesión.");
            }

            // Actualizar el usuario con los nuevos datos
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuarioAutenticado, username, correo, password);

            // Actualizar el objeto usuario en la sesión
            session.setAttribute("usuario", usuarioActualizado);

            model.addAttribute("mensaje", "Tus datos se han actualizado correctamente.");
            return "perfil"; // Página de perfil
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "perfil"; // Página de perfil con error
        }
    }


}