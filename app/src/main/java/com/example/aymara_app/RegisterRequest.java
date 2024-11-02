package com.example.aymara_app;

public class RegisterRequest {
    private String email;
    private String password;

    private String confirpass;
    private String username;
    private String first_name;
    private String last_name;

    private boolean is_active = true;
    private boolean is_staff = false;
    private boolean is_superuser = false;

    public RegisterRequest(String email, String password, String confirpass, String username, String first_name, String last_name) {
        this.email = email;
        this.password = password;
        this.confirpass = confirpass;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirpass() { return confirpass; }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public boolean isActive() {
        return is_active;
    }

    public boolean isStaff() {
        return is_staff;
    }

    public boolean isSuperuser() {
        return is_superuser;
    }
}
