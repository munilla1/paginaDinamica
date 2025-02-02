package com.repository;

import com.model.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByNombre(String nombre);
    
    // Buscar un usuario por su correo
    Optional<Usuario> findByCorreo(String correo);

    // Verificar si un correo ya existe
    boolean existsByCorreo(String correo);
    
    @Modifying
    @Transactional
    void deleteByCorreo(String correo);

}
