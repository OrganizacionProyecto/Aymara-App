package com.example.aymara_app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.aymara_app.Product;
import com.example.aymara_app.network.ApiClient;
import com.example.aymara_app.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {

    private ApiService apiService;

    public ProductRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<List<Product>> getProducts() {
        MutableLiveData<List<Product>> productData = new MutableLiveData<>();

        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) {
                        Log.d("ProductRepository", "Productos obtenidos: " + response.body().size());
                        productData.setValue(response.body());
                    } else {
                        Log.d("ProductRepository", "La respuesta es exitosa pero sin productos");
                        productData.setValue(new ArrayList<>()); // O puedes establecer null
                    }
                } else {
                    Log.e("ProductRepository", "Error en la respuesta: " + response.code());
                    productData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("ProductRepository", "Error al obtener productos: " + t.getMessage());
                productData.setValue(null);
            }
        });

        return productData;
    }
}