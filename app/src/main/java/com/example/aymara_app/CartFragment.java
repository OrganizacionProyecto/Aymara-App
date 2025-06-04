package com.example.aymara_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aymara_app.network.ApiClient;
import com.example.aymara_app.network.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private SharedPreferences prefs;
    private RecyclerView recyclerView;
    private TextView totalCarrito;
    private Button btnRealizarPedido;
    private CartAdapter adapter;
    private List<CartItem> carrito = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cart, container, false);
        prefs = requireContext().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);

        recyclerView = v.findViewById(R.id.recyclerCart);
        totalCarrito = v.findViewById(R.id.totalCarrito);
        btnRealizarPedido = v.findViewById(R.id.btnRealizarPedido);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        apiService = ApiClient.getClient().create(ApiService.class);

        adapter = new CartAdapter(carrito, new CartAdapter.CartActionListener() {
            @Override
            public void onModificarCantidad(CartItem item, String accion, int nuevaCantidad) {
                String accessToken = prefs.getString("access_token", "");
                if (accessToken.isEmpty()) {
                    showToast("Sesión expirada. Por favor, inicia sesión nuevamente.");
                    return;
                }

                HashMap<String, Object> body = new HashMap<>();
                body.put("accion", accion);
                body.put("cantidad", nuevaCantidad);

                apiService.updateCartItem(item.getId_producto(), body, "Bearer " + accessToken)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    obtenerCarrito();
                                } else {
                                    showToast("Error al actualizar cantidad");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                showToast("Error de red al actualizar carrito.");
                            }
                        });
            }

            @Override
            public void onEliminarProducto(CartItem item) {
                String accessToken = prefs.getString("access_token", "");
                if (accessToken.isEmpty()) {
                    showToast("Sesión expirada. Por favor, inicia sesión nuevamente.");
                    return;
                }

                apiService.deleteCartItem(item.getId_producto(), "Bearer " + accessToken)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    obtenerCarrito();
                                } else {
                                    showToast("Error al eliminar producto");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                showToast("Error de red al eliminar producto.");
                            }
                        });
            }
        });

        recyclerView.setAdapter(adapter);

        btnRealizarPedido.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_cartFragment_to_pedidoFragment);
        });


        obtenerCarrito();

        return v;
    }

    private void obtenerCarrito() {
        String accessToken = prefs.getString("access_token", "");
        if (accessToken.isEmpty()) {
            showToast("Sesión expirada. Por favor, inicia sesión nuevamente.");
            return;
        }

        apiService.getCart("Bearer " + accessToken).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();

                    carrito.clear();
                    carrito.addAll(cartResponse.getDetalles_producto());
                    adapter.notifyDataSetChanged();

                    totalCarrito.setText(String.format("Total: $%.2f", cartResponse.getTotal_carrito()));
                } else {
                    showToast("Error al obtener carrito.");
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                showToast("Error de red: " + t.getMessage());
            }
        });
    }

    private void showToast(final String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show());
        }
    }
}
