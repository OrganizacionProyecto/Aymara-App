package com.example.aymara_app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aymara_app.repository.ProductRepository;


public class ProductFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProductRepository productRepository;

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product, container, false);

        // Inicializar el RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar el adaptador y establecerlo en el RecyclerView
        productAdapter = new ProductAdapter();
        recyclerView.setAdapter(productAdapter);

        // Inicializar el repositorio y obtener productos desde la API
        productRepository = new ProductRepository();
        productRepository.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                productAdapter.setProductList(products);
                Log.d("ProductFragment", "Productos obtenidos: " + products.size());
            } else {
                Log.d("ProductFragment", "No se obtuvieron productos de la API");
            }
        });

        return view;
    }
}
