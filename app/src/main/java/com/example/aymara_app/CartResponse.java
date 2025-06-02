package com.example.aymara_app;

import java.util.List;

public class CartResponse {
    private List<CartItem> detalles_producto;
    private double total_carrito;

    public List<CartItem> getDetalles_producto() {
        return detalles_producto;
    }

    public void setDetalles_producto(List<CartItem> detalles_producto) {
        this.detalles_producto = detalles_producto;
    }

    public double getTotal_carrito() {
        return total_carrito;
    }

    public void setTotal_carrito(double total_carrito) {
        this.total_carrito = total_carrito;
    }
}
