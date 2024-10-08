package com.example.aymara_app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PoductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar la vista del fragmento
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        // Inicializar el RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crear una lista de productos de ejemplo
        List<Product> productList = new ArrayList<>();
        productList.add(new Product("Aceite de Cocos", "Suplemento dietario a base de aceite de coco. 100% natural", R.drawable.aceite_coco));
        productList.add(new Product("Garcimax Slim", " Suplemento dietario natural de heirbas (garcinia cambogia, fucus vesiculoso, té verde y café verde) y vitamina B1", R.drawable.garcimax));
        productList.add(new Product("Café Verde Plus", "Suplemento dietario a base de café verde, vitamina B6, L-carnitina, té verde y garcinia cambogia", R.drawable.cafe_verde));



        // Imprimir el tamaño de la lista en la consola para verificar
        Log.d("ProductFragment", "Tamaño de la lista de productos: " + productList.size());

        // Configurar el adaptador
        ProductAdapter productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        return view;
    }
}
