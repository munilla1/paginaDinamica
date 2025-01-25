package com.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.model.Usuario;
import com.service.UsuarioService;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

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


    @GetMapping("/usuario")
    public ResponseEntity<?> obtenerUsuarioActual(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario != null) {
            return ResponseEntity.ok(usuario); // 🔹 Devolver JSON en lugar de un objeto directo
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay usuario en sesión");
        }
    }



}


