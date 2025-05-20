package com.example.aymara_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aymara_app.R;
import com.example.aymara_app.CartAdapter;
import com.example.aymara_app.CartItem;
import com.example.aymara_app.network.ApiClient;
import com.example.aymara_app.network.ApiService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView totalCarrito;
    private Button btnRealizarPedido;
    private CartAdapter adapter;
    private List<CartItem> carrito = new ArrayList<>();
    private ApiService apiService;
    private String token = "Bearer TU_TOKEN_JWT";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = v.findViewById(R.id.recyclerCart);
        totalCarrito = v.findViewById(R.id.totalCarrito);
        btnRealizarPedido = v.findViewById(R.id.btnRealizarPedido);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        apiService = ApiClient.getClient().create(ApiService.class);

        adapter = new CartAdapter(carrito, new CartAdapter.CartActionListener() {
            @Override
            public void onCantidadModificada(CartItem item, int nuevaCantidad) {
                HashMap<String, Integer> body = new HashMap<>();
                body.put("cantidad", nuevaCantidad);

                apiService.updateCartItem(item.getId_producto(), body, token).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        obtenerCarrito();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {}
                });
            }

            @Override
            public void onEliminarProducto(CartItem item) {
                apiService.deleteCartItem(item.getId_producto(), token).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        obtenerCarrito();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {}
                });
            }
        });

        recyclerView.setAdapter(adapter);

        btnRealizarPedido.setOnClickListener(v1 -> {
            apiService.createPedido(token).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    obtenerCarrito(); // Reiniciar carrito
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
        });

        obtenerCarrito();

        return v;
    }

    private void obtenerCarrito() {
        apiService.getCart(token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    Type listType = new TypeToken<List<CartItem>>() {}.getType();
                    carrito.clear();
                    carrito.addAll(new Gson().fromJson(json, listType));
                    adapter.notifyDataSetChanged();
                    recalcularTotal();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    private void recalcularTotal() {
        double total = 0;
        for (CartItem item : carrito) {
            total += item.getPrecio_unitario() * item.getCantidad();
        }
        totalCarrito.setText(String.format("Total: $%.2f", total));
    }
}
