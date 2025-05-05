package com.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * @Component public class CustomAuthenticationFilter extends
 * OncePerRequestFilter {
 * 
 * @Autowired private UserDetailsServiceImpl userDetailsServiceImpl;
 * 
 * @Override protected void doFilterInternal(HttpServletRequest request,
 * HttpServletResponse response, FilterChain filterChain) throws
 * ServletException, IOException {
 * 
 * // Si ya hay autenticación, no hacemos nada Authentication existingAuth =
 * SecurityContextHolder.getContext().getAuthentication(); if (existingAuth ==
 * null || existingAuth instanceof AnonymousAuthenticationToken) {
 * 
 * // Tu lógica para extraer el usuario (cookie, header, etc.) Authentication
 * authentication = userDetailsServiceImpl.createAuthentication(request);
 * 
 * if (authentication != null && authentication.isAuthenticated()) {
 * SecurityContextHolder.getContext().setAuthentication(authentication); } }
 * 
 * filterChain.doFilter(request, response); } }
 */

