package com.example.aymara_app.network;

import com.example.aymara_app.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/tablas/productos/")
    Call<List<Product>> getProducts();
}
