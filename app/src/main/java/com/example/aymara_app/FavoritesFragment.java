package com.example.aymara_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aymara_app.network.ApiClient;
import com.example.aymara_app.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.favorites_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        apiService = ApiClient.getClient().create(ApiService.class);
        productAdapter = new ProductAdapter(true, requireContext());
        recyclerView.setAdapter(productAdapter);

        fetchFavorites();
    }

    private void fetchFavorites() {
        String token = getAccessToken(requireContext());

        if (token.isEmpty()) {
            Toast.makeText(getContext(), "Debes iniciar sesión para ver tus favoritos", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getFavorites("Bearer " + token).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> favoriteProducts = response.body();
                    if (!favoriteProducts.isEmpty()) {
                        productAdapter.setProductList(favoriteProducts, requireContext());
                    } else {
                        Toast.makeText(getContext(), "No tienes productos en favoritos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String error = "Error al obtener favoritos: " + response.code() + " - " + response.message();
                    Log.e("FavoritesFragment", error);
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                Log.e("FavoritesFragment", "Fallo de conexión: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Error de conexión al obtener favoritos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        return prefs.getString("access_token", "");
    }
}
