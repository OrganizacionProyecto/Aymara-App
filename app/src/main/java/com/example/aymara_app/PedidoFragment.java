package com.example.aymara_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.aymara_app.network.ApiClient;
import com.example.aymara_app.network.ApiService;
/* import com.example.aymara_app.PedidoResponse; */

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoFragment extends Fragment {

    private EditText editDireccion, editTelefono;
    private Button btnRealizarPedido, btnDescargarPDF;

    private Integer pedidoId = null;

    private ApiService apiService;

    public PedidoFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedido, container, false);

        editDireccion = view.findViewById(R.id.editDireccion);
        editTelefono = view.findViewById(R.id.editTelefono);
        btnRealizarPedido = view.findViewById(R.id.btnRealizarPedido);
        btnDescargarPDF = view.findViewById(R.id.btnDescargarPDF);

        apiService = ApiClient.getClient().create(ApiService.class);

        btnRealizarPedido.setOnClickListener(v -> realizarPedido());
        btnDescargarPDF.setOnClickListener(v -> {
            if (pedidoId != null) descargarPDF(pedidoId);
        });

        return view;
    }

    private String obtenerToken() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        return prefs.getString("access_token", null);
    }

    private void realizarPedido() {
        String direccion = editDireccion.getText().toString();
        String telefono = editTelefono.getText().toString();
        String metodoPago = "mercadopago";  // fijo

        if (TextUtils.isEmpty(direccion) || TextUtils.isEmpty(telefono)) {
            Toast.makeText(getContext(), "Completa dirección y teléfono", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = obtenerToken();
        if (token == null) {
            Toast.makeText(getContext(), "No autenticado, por favor inicia sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> datosPedido = new HashMap<>();
        datosPedido.put("direccion_entrega", direccion);
        datosPedido.put("telefono", telefono);
        datosPedido.put("metodo_pago", metodoPago);

        String authHeader = "Bearer " + token;

        apiService.createPedido(datosPedido, authHeader).enqueue(new Callback<PedidoResponse>() {
            @Override
            public void onResponse(@NonNull Call<PedidoResponse> call, @NonNull Response<PedidoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PedidoResponse body = response.body();
                    pedidoId = body.getId();

                    if (body.getMercadopagoUrl() != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(body.getMercadopagoUrl())));
                        return;
                    }

                    Toast.makeText(getContext(), "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show();
                    btnDescargarPDF.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(() -> {
                        // Aquí puedes redirigir o limpiar el formulario si quieres
                    }, 3000);
                } else {
                    Toast.makeText(getContext(), "Error al realizar el pedido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PedidoResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void descargarPDF(int pedidoId) {
        apiService.getFactura(pedidoId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        File pdfFile = new File(requireContext().getExternalFilesDir(null),
                                "pedido_" + pedidoId + ".pdf");
                        InputStream inputStream = response.body().byteStream();
                        FileOutputStream outputStream = new FileOutputStream(pdfFile);
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.close();

                        Uri uri = FileProvider.getUriForFile(
                                requireContext(),
                                requireContext().getPackageName() + ".provider",
                                pdfFile);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al guardar PDF", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error al descargar PDF", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
