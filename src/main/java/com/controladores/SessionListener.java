package com.controladores;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionListener implements HttpSessionListener {

    @Autowired
    private HttpServletRequest request;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // Lógica cuando la sesión se crea (opcional)
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // Lógica cuando la sesión se destruye (incluye la expiración)
        System.out.println("🔴 La sesión ha expirado o se ha cerrado.");
        
        // Obtener la sesión de la solicitud actual
        HttpSession session = se.getSession();
        
        if (session != null && session.getAttribute("usuario") != null) {
            // Invalidar la sesión como si fuera un logout
            session.invalidate();
        }
    }
}


