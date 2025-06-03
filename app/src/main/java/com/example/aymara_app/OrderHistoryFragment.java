package com.example.aymara_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aymara_app.network.ApiService;
import com.example.aymara_app.network.ApiClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private ApiService apiService;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdapter();
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);
        prefs = getActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);

        loadOrders();

        return view;
    }

    private void loadOrders() {
        String accessToken = prefs.getString("access_token", "");
        apiService.getHistorialPedidos("Bearer " + accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseString);
                        List<Order> orders = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String date = jsonObject.getString("fecha");
                            double total = jsonObject.getDouble("total");
                            String status = jsonObject.getString("estado");
                            orders.add(new Order(id, date, total, status));
                        }

                        adapter.setOrders(orders);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error al procesar los pedidos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error al cargar los pedidos: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}