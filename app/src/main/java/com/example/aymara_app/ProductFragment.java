package com.example.aymara_app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aymara_app.repository.ProductRepository;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.lifecycle.Observer;
import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProductRepository productRepository;
    private List<Product> productList = new ArrayList<>();
    private EditText searchBar;

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product, container, false);

        // Inicializar el RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // Inicializar el adaptador y establecerlo en el RecyclerView
        productAdapter = new ProductAdapter();
        recyclerView.setAdapter(productAdapter);

        // Inicializar la barra de búsqueda
        searchBar = view.findViewById(R.id.search_bar);
        setupSearchBar();

        // Inicializar el repositorio y obtener productos desde la API
        productRepository = new ProductRepository();
        productRepository.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                Log.d("ProductFragment", "Productos obtenidos: " + products.size());
                productList = products;  // Guardar la lista de productos
                productAdapter.setProductList(products);
            } else {
                Log.d("ProductFragment", "No se obtuvieron productos de la API");
            }
        });

        // Botón para mostrar solo favoritos
        ImageButton favoriteButton = view.findViewById(R.id.button);
        favoriteButton.setOnClickListener(v -> {
            String token = "tu_token_aqui"; // Reemplaza esto por tu método para obtener el token

            productRepository.getFavorites(token).observe(getViewLifecycleOwner(), favorites -> {
                if (favorites != null && !favorites.isEmpty()) {
                    productList = favorites;  // Guardar la lista de favoritos
                    productAdapter.setProductList(favorites);
                    Log.d("ProductFragment", "Mostrando productos favoritos: " + favorites.size());
                } else {
                    Log.d("ProductFragment", "No hay productos favoritos");
                }
            });
        });

        return view;
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita implementar
            }
        });
    }

    private void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getNombre().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(product);
            }
        }
        productAdapter.setProductList(filteredList);
    }
}
