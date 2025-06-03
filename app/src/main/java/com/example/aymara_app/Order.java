package com.example.aymara_app;

public class Order {
    private int id;
    private int usuario;
    private String direccionEntrega;
    private String telefono;
    private String metodoPago;
    private double total;
    private String fechaCreacion;

    public Order(int id, int usuario, String direccionEntrega, String telefono, String metodoPago, double total, String fechaCreacion) {
        this.id = id;
        this.usuario = usuario;
        this.direccionEntrega = direccionEntrega;
        this.telefono = telefono;
        this.metodoPago = metodoPago;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
    }

    public int getId() { return id; }
    public int getUsuario() { return usuario; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public String getTelefono() { return telefono; }
    public String getMetodoPago() { return metodoPago; }
    public double getTotal() { return total; }
    public String getFechaCreacion() { return fechaCreacion; }
}