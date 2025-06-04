package com.example.aymara_app;

import com.google.gson.annotations.SerializedName;

public class PedidoResponse {

    @SerializedName("id")
    private Integer id;

    @SerializedName("mercadopago_url")
    private String mercadopagoUrl;

    public Integer getId() {
        return id;
    }

    public String getMercadopagoUrl() {
        return mercadopagoUrl;
    }
}