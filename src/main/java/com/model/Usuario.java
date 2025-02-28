package com.model;

import java.io.Serializable;


import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class Usuario implements Serializable {
	
	private static final long serialVersionUID = 1L; // Requerido para serialización

    @Id
    private String username;
    
    private String password;
    private boolean enabled = true;

    public Usuario() {}

    public Usuario(String username, String password, boolean enabled) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    // Getters y Setters

    public String getUsername() { return username; }
    public void setusername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setpassword(String password) { this.password = password; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

}
