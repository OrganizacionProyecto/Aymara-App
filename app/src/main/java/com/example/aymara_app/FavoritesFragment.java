package com.example.aymara_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView favoritesRecyclerView;
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesRecyclerView = view.findViewById(R.id.recycler_view_favorites);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Obtener si el usuario está logueado desde SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false); // Ajusta la clave según tu implementación

        // Inicializar el adaptador con el contexto
        productAdapter = new ProductAdapter(isLoggedIn, requireContext());
        favoritesRecyclerView.setAdapter(productAdapter);

        loadFavorites(); // Método para cargar los favoritos

        return view;
    }

    private void loadFavorites() {
        // Aquí iría la lógica para cargar la lista de productos favoritos
    }
}
