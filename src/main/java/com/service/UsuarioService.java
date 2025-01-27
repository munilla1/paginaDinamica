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
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new Exception("El correo ya está registrado. Usa otro correo.");
        }
        // Encriptar la contraseña antes de guardarla
        System.out.println("Encriptando contraseña...");
        usuario.setContras(passwordEncoder.encode(usuario.getContras()));
        
        System.out.println("Guardando en BD: " + usuario);
        return usuarioRepository.save(usuario);
    }
    
    public Usuario buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }
    
    
    public boolean validarCredenciales(String correo, String contrasenaIngresada) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            return passwordEncoder.matches(contrasenaIngresada, usuario.getContras());
        }

        return false; // Usuario no encontrado
    }

	public Usuario obtenerPorCorreo(String correo) {
		return usuarioRepository.findByCorreo(correo).orElse(null);
	}
	
	@Transactional
	public void eliminarUsuario(String correo) {
	    usuarioRepository.deleteByCorreo(correo);
	}



}
