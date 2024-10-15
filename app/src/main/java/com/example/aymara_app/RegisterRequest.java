package com.example.aymara_app;

// Clase para el registro del usuario
public class RegisterRequest {
    private String email;
    private String password;
    private String username;

    // Constructor
    public RegisterRequest(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
