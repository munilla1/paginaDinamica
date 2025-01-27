package com.controladores;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/registro-login")
    public String login() {
        return "registro-login"; // Busca WEB-INF/views/registro-login.jsp
    }


    @GetMapping("/DarDeBaja")
    public String mostrarFormularioBaja(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "DarDeBaja";
    }
    
    
	/*
	 * @GetMapping("/acceso") public String accesoCorrecto(HttpSession session,
	 * Model model) { Usuario usuario = (Usuario) session.getAttribute("usuario");
	 * 
	 * if (usuario == null) { return "redirect:/index"; // ✅ Redirigir a inicio en
	 * vez de login }
	 * 
	 * model.addAttribute("usuario", usuario); return "accesoCorrecto"; // ✅ Mostrar
	 * la vista }
	 */



	/*
	 * @GetMapping("/perfil") public String verPerfil(HttpSession session, Model
	 * model) { Usuario usuario = (Usuario) session.getAttribute("usuario");
	 * 
	 * if (usuario != null) { model.addAttribute("usuario", usuario); return
	 * "perfil"; } else { return "redirect:/registro-login"; // ✅ Redirige en lugar
	 * de devolver la vista } }
	 */

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
    public String validarUsuario(@RequestParam String correo, 
                                 @RequestParam String contrasenaIngresada, 
                                 HttpSession session, 
                                 Model model) {
        try {
            System.out.println("🔹 Intentando autenticación para: " + correo);

            // Buscar usuario en la base de datos
            Usuario usuario = usuarioService.obtenerPorCorreo(correo);

            if (usuario == null || !passwordEncoder.matches(contrasenaIngresada, usuario.getContras())) {
                throw new Exception("❌ Correo o contraseña incorrectos.");
            }

            System.out.println("✅ Usuario autenticado correctamente: " + usuario.getCorreo());
            session.setAttribute("usuario", usuario); // 🔐 Guarda el usuario en la sesión manualmente
            
            return "accesoCorrecto"; // ✅ Redirige a accesoCorrecto si la autenticación es exitosa

        } catch (Exception e) {
            System.out.println("❌ Error de autenticación: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "registro-login"; // ✅ Devuelve la vista con el mensaje de error
        }
    }

    @PostMapping("/eliminar")
    public String eliminarUsuario(@RequestParam String correo, 
                                  @RequestParam String contrasenaIngresada, 
                                  Model model, 
                                  HttpSession session) {
        try {
            System.out.println("🔹 Intentando eliminar usuario con correo: " + correo);

            // Buscar usuario en la base de datos
            Usuario usuario = usuarioService.obtenerPorCorreo(correo);


            // Validar la contraseña encriptada
            if (!passwordEncoder.matches(contrasenaIngresada, usuario.getContras())) {
                throw new BadCredentialsException("❌ Correo o contraseña incorrectos.");
            }

            // Eliminar el usuario
            usuarioService.eliminarUsuario(usuario.getCorreo());

            // Invalidar la sesión
            session.invalidate();

            System.out.println("✅ Usuario eliminado correctamente: " + correo);

            return "usuarioEliminado"; // ✅ Redirige si la eliminación es exitosa

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



