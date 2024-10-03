package com.example.aymara_app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.aymara_app.Product;
import com.example.aymara_app.network.ApiClient;
import com.example.aymara_app.network.ApiService;

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
                    productData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                productData.setValue(null);
            }
        });

        return productData;
    }
}
