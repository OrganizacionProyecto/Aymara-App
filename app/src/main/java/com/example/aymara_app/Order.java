package com.example.aymara_app;

public class Order {
    private int id;
    private String fecha;
    private double total;
    private String estado;

    public Order(int id, String fecha, double total, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getFecha() { return fecha; }
    public double getTotal() { return total; }
    public String getEstado() { return estado; }
}