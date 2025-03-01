package com.repository;

import com.model.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByUsername(String username);
    
	boolean existsByCorreo(String email);
	
	Optional<Usuario> findById(Long id);

    // Verificar si un correo ya existe
    boolean existsByUsername(String username);
    
    @Modifying
    @Transactional
    void deleteByUsername(String username);

}
