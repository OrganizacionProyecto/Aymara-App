package com.example.aymara_app;

public class RegisterRequest {
    private String email;
    private String password;
    private String username;
    private String first_name;
    private String last_name;

    private boolean is_active = true;
    private boolean is_staff = false;
    private boolean is_superuser = false;

    // Campos nuevos según la API
    private String direccion;
    private String password2;
    private String current_password;

    public RegisterRequest(String email, String password, String username, String first_name, String last_name) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    // Getters
    public String getEmail() {return email;}

    public String getPassword() {return password;}

    public String getUsername() {return username;}

    public String getFirstName() {return first_name;}

    public String getLastName() {return last_name;}

    public String getDireccion() {return direccion;}

    public String getPassword2() {return password2;}

    public String getCurrentPassword() {return current_password;}


    public boolean isActive() {return is_active;}

    public boolean isStaff() {return is_staff;}

    public boolean isSuperuser() {return is_superuser;}

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public void setIsActive(boolean is_active) {
        this.is_active = is_active;
    }

    public void setStaff(boolean is_staff) {
        this.is_staff = is_staff;
    }

    public void setSuperuser(boolean is_superuser) {
        this.is_superuser = is_superuser;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public void setCurrentPassword(String current_password) {
        this.current_password = current_password;
    }
}




