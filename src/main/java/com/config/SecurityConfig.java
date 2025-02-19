package com.config;

import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.model.Usuario;
import com.repository.UsuarioRepository;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(request -> request
        		.requestMatchers("/error", "/", "/registro-login", "/acceso", "/accesoCorrecto", "/guardar", "/datosGuardados").permitAll() // 🔥 Permitir acceso sin login
        		.requestMatchers("/DarDeBaja").authenticated()
        		.requestMatchers("/WEB-INF/views/**").permitAll() // ✅ Permitir acceso a JSPs
                .requestMatchers("/css/**", "/imagenes/**").permitAll() // ✅ Permitir archivos estáticos
                .anyRequest().authenticated() // 🔐 Todo lo demás requiere autenticación
	        )
	        
	        .httpBasic(Customizer.withDefaults())
				 
            .logout(logout -> logout
                .logoutUrl("/logout") // 🔹 Ruta para cerrar sesión
                .logoutSuccessUrl("/") // 🔹 Redirigir a login después de cerrar sesión
                .invalidateHttpSession(true) // 🔹 Invalidar sesión
                .deleteCookies("JSESSIONID") // 🔹 Eliminar cookies de sesión
            );

        logger.info("🔹 Configuración de seguridad aplicada correctamente");
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(
                "/css/**", "/js/**", "/imagenes/**", "/static/**", "/videos/**"
            );
    }
    
	/*
	 * @Bean public UserDetailsService userDetailsService(DataSource dataSource) {
	 * return new JdbcUserDetailsManager(dataSource); }
	 */
    
	/*
	 * @Bean public UserDetailsService testUser(PasswordEncoder passwordEncoder) {
	 * User.UserBuilder user = User.builder(); UserDetails user1 =
	 * user.username("sara") .password(passwordEncoder.encode("1234")) .roles()
	 * .build(); return new InMemoryUserDetailsManager(user1); }
	 */
    
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return username -> {
            // Buscar el usuario en la base de datos
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();

                // Asegurarte de que el usuario esté habilitado (por ejemplo, el campo "enabled")
                return User.builder()
                        .username(usuario.getusername())
                        .password(usuario.getpassword())  // La contraseña ya está encriptada en la base de datos
                        .roles(usuario.isEnabled() ? "USER" : "DISABLED")  // Puedes agregar roles adicionales aquí
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(!usuario.isEnabled())
                        .build();
            } else {
                throw new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username);
            }
        };
    }

    
    
    
    
}

