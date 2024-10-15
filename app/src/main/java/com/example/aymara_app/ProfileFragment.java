package com.example.aymara_app;

import com.example.aymara_app.network.ApiService;
import com.example.aymara_app.network.ApiClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private EditText etFirst_Name, etLast_Name, etEmail, etAddress; // Eliminar etUsername
    private Button btnChangePassword, btnDeleteAccount, btnLogout;
    private ApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        etFirst_Name = view.findViewById(R.id.etFirst_Name);
        etLast_Name = view.findViewById(R.id.etLast_Name);
        etEmail = view.findViewById(R.id.etEmail);
        etAddress = view.findViewById(R.id.etAddress);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        btnLogout = view.findViewById(R.id.btnOut);

        apiService = ApiClient.getClient().create(ApiService.class);

        loadUserData();

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
        btnLogout.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void loadUserData() {
        SharedPreferences prefs = getActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", ""); // Asegúrate de usar el mismo nombre de clave

        apiService.getUserDetails("Bearer " + accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);

                        etFirst_Name.setText(jsonObject.getString("first_name"));
                        etLast_Name.setText(jsonObject.getString("last_name"));
                        etEmail.setText(jsonObject.getString("email"));
                        etAddress.setText(jsonObject.getString("address")); // Asegúrate de que este campo existe en la respuesta

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error al cargar los datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cambiar Contraseña");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16); // Padding para el layout

        EditText etNewPassword = new EditText(getActivity());
        etNewPassword.setHint("Nueva Contraseña");
        etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNewPassword);

        EditText etConfirmPassword = new EditText(getActivity());
        etConfirmPassword.setHint("Confirmar Contraseña");
        etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etConfirmPassword);

        builder.setView(layout)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String newPassword = etNewPassword.getText().toString();
                    String confirmPassword = etConfirmPassword.getText().toString();
                    if (newPassword.equals(confirmPassword) && isValidPassword(newPassword)) {
                        updatePassword(newPassword);
                    } else {
                        Toast.makeText(getActivity(), "Las contraseñas no coinciden o son inválidas", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6; // Ejemplo de validación
    }

    private void updatePassword(String newPassword) {
        SharedPreferences prefs = getActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", ""); // Obtener el token de acceso

        JSONObject json = new JSONObject();
        try {
            json.put("new_password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiService.changePassword("Bearer " + accessToken, json.toString()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Eliminar Cuenta")
                .setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    deleteAccount();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void deleteAccount() {
        SharedPreferences prefs = getActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", ""); // Obtener el token de acceso

        apiService.deleteAccount("Bearer " + accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Cuenta eliminada con éxito", Toast.LENGTH_SHORT).show();
                    logoutUser();
                } else {
                    Toast.makeText(getActivity(), "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutUser() {
        SharedPreferences prefs = getActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", "");

        apiService.logout("Bearer " + accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    prefs.edit().clear().apply();
                    Toast.makeText(getActivity(), "Sesión cerrada con éxito", Toast.LENGTH_SHORT).show();
                    // Redirigir a la pantalla de inicio o login
                    // Aquí puedes implementar la lógica para redirigir a la pantalla de inicio o login
                } else {
                    Toast.makeText(getActivity(), "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
