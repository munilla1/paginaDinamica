package com.service;

import com.model.Usuario;
import com.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) throws Exception {
        // Verificar si el correo ya está registrado
        if (usuarioRepository.existsByUsername(usuario.getusername())) {
            throw new Exception("El correo ya está registrado. Usa otro correo.");
        }
        
        // Validar que la contraseña no sea null o vacía
        if (usuario.getpassword() == null || usuario.getpassword().trim().isEmpty()) {
            throw new Exception("La contraseña no puede estar vacía.");
        }
        
        // Encriptar la contraseña antes de guardarla
        System.out.println("Encriptando contraseña...");
        usuario.setpassword(passwordEncoder.encode(usuario.getpassword()));
        usuario.setEnabled(true);
        
        System.out.println("Guardando en BD: " + usuario);
        return usuarioRepository.save(usuario);
    }

    
    public Usuario buscarPorNombre(String username) throws Exception {
    	Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
    	if (usuarioOpt.isPresent()) {
    	    Usuario usuario = usuarioOpt.get();
    	    return usuario;
    	} else {
    	    throw new Exception("Usuario no encontrado");
    	}

    }
    
    
    public boolean validarCredenciales(String username, String contrasenaIngresada) throws Exception {
        // Verificar que el username no sea null o vacío
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede ser null o vacío");
        }
        
        // Verificar que la contraseña no sea null o vacía
        if (contrasenaIngresada == null || contrasenaIngresada.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser null o vacía");
        }

        // Buscar el usuario por username
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        // Si el usuario existe
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verificar si la contraseña es correcta
            return passwordEncoder.matches(contrasenaIngresada, usuario.getpassword());
        } else {
            // Lanzar excepción si el usuario no se encuentra
            throw new Exception("Usuario no encontrado");
        }
    }




	public Usuario obtenerPorUsername(String username) throws Exception {
		Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
		if (usuarioOpt.isPresent()) {
		    Usuario usuario = usuarioOpt.get();
		    return usuario;
		} else {
		    throw new Exception("Usuario no encontrado");
		}

	}
	
	@Transactional
	public void eliminarUsuario(String username) {
	    usuarioRepository.deleteByUsername(username);
	}



}
