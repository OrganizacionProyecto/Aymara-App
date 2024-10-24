package com.example.aymara_app.repository;

import android.util.Log;
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
                    Log.d("API", "Productos recibidos: " + response.body());
                    productData.setValue(response.body());
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API", "Error en la llamada: " + t.getMessage());
                productData.setValue(null);
            }
        });

        return productData;
    }

    // Método para obtener favoritos
    public LiveData<List<Product>> getFavorites(String token) {
        MutableLiveData<List<Product>> favoritesData = new MutableLiveData<>();

        // Llamada a la API para obtener los favoritos, pasando el token
        apiService.getFavorites(token).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Favoritos recibidos: " + response.body());
                    favoritesData.setValue(response.body());
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API", "Error en la llamada: " + t.getMessage());
                favoritesData.setValue(null);
            }
        });

        return favoritesData;
    }
}