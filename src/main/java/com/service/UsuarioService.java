package com.service;

import com.model.Usuario;
import com.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public boolean existsByUsername(String username) {
        return usuarioRepository.findByUsername(username).isPresent();
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("Usuario no encontrado: " + username);
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });
    }

    public void save(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public void delete(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }
    
    
}
