package com.example.aymara_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aymara_app.network.ApiClient;
import com.example.aymara_app.network.ApiService;
import com.example.aymara_app.repository.ProductRepository;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProductRepository productRepository;
    private List<Product> productList = new ArrayList<>();
    private EditText searchBar;
    private SharedPreferences sharedPreferences;
    private Spinner categorySpinner;
    private String selectedCategory = "";
    private List<Categoria> categoriaList = new ArrayList<>();
    public ProductFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        sharedPreferences = getContext().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);

        boolean isLoggedIn = checkIfUserIsLoggedIn();

        initializeRecyclerView(view, isLoggedIn);

        searchBar = view.findViewById(R.id.search_bar);
        setupSearchBar();

        initializeProductRepository(isLoggedIn, view);

        setupFavoriteButton(view, isLoggedIn);

        categorySpinner = view.findViewById(R.id.category_spinner);
        loadCategorias();


        return view;
    }

    private void loadCategorias() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getNombre().enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriaList.clear();

                    // Agrega una categoría "Todas"
                    Categoria todas = new Categoria();
                    todas.setId(0); // o 0
                    todas.setNombre("Todas");
                    categoriaList.add(todas);

                    categoriaList.addAll(response.body());

                    ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoriaList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);

                    categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCategory = categoriaList.get(position).getNombre();
                            filter(searchBar.getText().toString()); // Refiltra con la categoría
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedCategory = "Todas";
                        }
                    });
                } else {
                    Log.e("Categorias", "Respuesta inválida");
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Log.e("Categorias", "Error: " + t.getMessage());
            }
        });
    }

    private void initializeRecyclerView(View view, boolean isLoggedIn) {
        recyclerView = view.findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        productAdapter = new ProductAdapter(isLoggedIn, requireContext());
        recyclerView.setAdapter(productAdapter);
    }

    private void setupFavoriteButton(View view, boolean isLoggedIn) {
        ImageButton favoriteButton = view.findViewById(R.id.button);
        if (isLoggedIn) {
            favoriteButton.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.favoritesFragment);
            });
        } else {
            favoriteButton.setVisibility(View.GONE);
        }
    }


    private void initializeProductRepository(boolean isLoggedIn, View view) {
        productRepository = new ProductRepository();
        productRepository.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                Log.d("ProductFragment", "Productos obtenidos: " + products.size());
                productList = products;
                productAdapter.setProductList(products, requireContext());
            } else {
                Log.d("ProductFragment", "No se obtuvieron productos de la API");
            }
        });
    }

    private boolean checkIfUserIsLoggedIn() {
        String token = sharedPreferences.getString("access_token", null);
        return token != null;
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
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
        productAdapter.setProductList(filteredList, requireContext());
    }


    
    public void refreshProductList() {
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        productAdapter.setIsLoggedIn(isLoggedIn);
        productAdapter.notifyDataSetChanged();
    }


}