package com.example.aymara_app;

import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("id_producto")
    private int idProducto;

    private String nombre;
    private String descripcion;
    private double precio;

    private int stock;

    private String imagen;

    @SerializedName("id_categoria")
    private int idCategoria;

    private int cantidad;

    public Product() {
        this.cantidad = 0;  // Inicializa cantidad a 0 para UI
    }

    public Product(int idProducto, String nombre, String descripcion, double precio, int stock, String imagen, int idCategoria) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.imagen = imagen;
        this.idCategoria = idCategoria;
        this.cantidad = 0;
    }

    // Getters y setters

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    // Solo para UI (cantidad seleccionada)
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }


    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", imagen='" + imagen + '\'' +
                ", idCategoria=" + idCategoria +
                ", cantidad=" + cantidad +
                '}';
    }
}
