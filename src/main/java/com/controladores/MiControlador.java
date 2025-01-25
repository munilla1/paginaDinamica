package com.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.model.Usuario;
import com.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuarios")
public class MiControlador {
    
    @Autowired
    private UsuarioService usuarioService;
    

    // Página principal
    @GetMapping("")
    public String index() {
        return "index"; // Redirige a index.jsp en /WEB-INF/views/
    }
    
    // Mostrar el formulario de registro
    @GetMapping("/registro-login")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro-login"; // Carga WEB-INF/views/registro.jsp
    }
    
    @GetMapping("/DarDeBaja")
    public String mostrarFormularioBaja(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "DarDeBaja"; // Carga WEB-INF/views/DarDeBaja.jsp
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            usuarioService.guardarUsuario(usuario);
            return "datosGuardados"; // Redirige a la página de confirmación
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "registro-login"; // Vuelve al formulario con el mensaje de error
        }
    }

    @PostMapping("/acceso")
    public String validarUsuario(@RequestParam String correo, @RequestParam String contrasenaIngresada, HttpSession session, Model model) {
        Usuario usuario = usuarioService.obtenerPorCorreo(correo);

        if (usuario != null && usuarioService.validarCredenciales(correo, contrasenaIngresada)) {
            session.setAttribute("usuario", usuario); // 🔹 Se guarda en la base de datos
            return "accesoCorrecto";
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos.");
            return "registro-login";
        }
    }


    @PostMapping("/eliminar")
    public String eliminarUsuario(@RequestParam String correo, @RequestParam String contrasenaIngresada, Model model, HttpSession session) {
        try {
            Usuario usuario = usuarioService.obtenerPorCorreo(correo);

            // 🔹 Validar si el usuario existe y la contraseña es correcta
            if (usuario == null || !usuarioService.validarCredenciales(correo, contrasenaIngresada)) {
                throw new Exception("Correo o contraseña incorrectos."); // 🔹 Lanza una excepción si no son válidos
            }

            // 🔹 Eliminar usuario si las credenciales son correctas
            usuarioService.eliminarUsuario(usuario.getId());
            session.invalidate(); // 🔹 Cerrar la sesión después de la eliminación

            return "usuarioEliminado"; // 🔹 Redirige a la página de confirmación

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage()); // 🔹 Captura y envía el mensaje de error a la vista
            return "DarDeBaja"; // 🔹 Redirige a la página de error
        }
    }




}


