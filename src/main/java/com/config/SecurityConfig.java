package com.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth
        		.requestMatchers("/", "/registro-login", "/guardar", "/datosGuardados", "/acceso", "/accesoCorrecto", "/DarDeBaja", "/eliminar", "/usuarioEliminado", "/error", "/css/**").permitAll() // 🔥 Permitir acceso sin login
        		.requestMatchers("/WEB-INF/views/**").permitAll() // ✅ Permitir acceso a JSPs
                .requestMatchers("/css/**", "/imagenes/**").permitAll() // ✅ Permitir archivos estáticos
                .anyRequest().authenticated() // 🔐 Todo lo demás requiere autenticación
	        )
				 
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
}

