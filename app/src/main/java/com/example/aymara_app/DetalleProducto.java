package com.example.aymara_app.models;

import com.google.gson.annotations.SerializedName;

public class DetalleProducto {
    @SerializedName("id_producto")
    private int idProducto;

    @SerializedName("nombre_producto")
    private String nombreProducto;

    @SerializedName("precio_unitario")
    private double precioUnitario;

    private int cantidad;

    @SerializedName("total_producto")
    private double totalProducto;

    // Getters y setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getTotalProducto() { return totalProducto; }
    public void setTotalProducto(double totalProducto) { this.totalProducto = totalProducto; }
}
