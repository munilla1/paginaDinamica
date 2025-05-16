package com.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
@WebFilter(urlPatterns = "/pdfs/*")
public class PdfAccessFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Permitir acceso a otras rutas
        String requestURI = httpRequest.getRequestURI();
        if (!requestURI.startsWith("/pdfs/")) {
            chain.doFilter(request, response);
            return;
        }

        // Verificar si el usuario ha pagado
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("paymentIntentId") == null) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
            return;
        }

        chain.doFilter(request, response);
    }
}



