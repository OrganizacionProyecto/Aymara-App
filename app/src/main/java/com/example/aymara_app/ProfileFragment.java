package com.example.aymara_app;

import com.example.aymara_app.network.ApiService;
import com.example.aymara_app.network.ApiClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.annotation.SuppressLint;
import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.ResponseBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private EditText etUsername, etFirst_Name, etLast_Name, etEmail, etDireccion;
    private Button btnChangePassword, btnDeleteAccount, btnLogout;
    private Button btnEditUsername, btnEditDireccion;
    private ApiService apiService;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        // Inicialización de vistas
        etUsername = view.findViewById(R.id.etUsername);
        etFirst_Name = view.findViewById(R.id.etFirst_Name);
        etLast_Name = view.findViewById(R.id.etLast_Name);
        etEmail = view.findViewById(R.id.etEmail);
        etDireccion = view.findViewById(R.id.etDireccion);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        btnLogout = view.findViewById(R.id.btnOut);
        btnEditUsername = view.findViewById(R.id.btnEditUsername);
        btnEditDireccion = view.findViewById(R.id.btnEditDireccion);

        apiService = ApiClient.getClient().create(ApiService.class);

        loadUserData();

        // Configuración de listeners
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
        btnLogout.setOnClickListener(v -> logoutUser());
        btnEditUsername.setOnClickListener(v -> showChangeUsernameDialog());
        btnEditDireccion.setOnClickListener(v -> showChangeAddressDialog());
        etUsername.setEnabled(false);
        etFirst_Name.setEnabled(false);
        etLast_Name.setEnabled(false);
        etEmail.setEnabled(false);
        etDireccion.setEnabled(false);

        return view;
    }

    private void loadUserData() {
        SharedPreferences prefs = getActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", "");

        apiService.getUserDetails("Bearer " + accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try (ResponseBody responseBody = response.body()) {
                        String responseString = responseBody.string();
                        JSONObject jsonObject = new JSONObject(responseString);

                        // Establecer los valores en los EditText
                        etUsername.setText(jsonObject.getString("username"));
                        etFirst_Name.setText(jsonObject.getString("first_name"));
                        etLast_Name.setText(jsonObject.getString("last_name"));
                        etEmail.setText(jsonObject.getString("email"));
                        etDireccion.setText(jsonObject.getString("direccion"));

                        // Actualizar el TextView del título
                        TextView tvProfileTitle = getView().findViewById(R.id.tvProfileTitle);
                        String username = jsonObject.getString("username");
                        tvProfileTitle.setText("Perfil de " + username);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error al cargar los datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updatePassword(@NonNull String oldPassword, @NonNull String newPassword) {
        SharedPreferences prefs = getActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", "");

        JSONObject json = new JSONObject();
        try {
            json.put("old_password", oldPassword);
            json.put("new_password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        apiService.changePassword("Bearer " + accessToken, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Toast.makeText(getActivity(), "Error: " + errorResponse, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cambiar Contraseña");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        EditText etOldPassword = new EditText(getActivity());
        etOldPassword.setHint("Contraseña Antigua");
        etOldPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etOldPassword);

        EditText etNewPassword = new EditText(getActivity());
        etNewPassword.setHint("Nueva Contraseña");
        etNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNewPassword);

        builder.setView(layout)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String oldPassword = etOldPassword.getText().toString();
                    String newPassword = etNewPassword.getText().toString();
                    if (!newPassword.isEmpty() && !oldPassword.isEmpty()) {
                        updatePassword(oldPassword, newPassword);
                    } else {
                        Toast.makeText(getActivity(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showChangeAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cambiar Dirección");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        EditText etNewAddress = new EditText(getActivity());
        etNewAddress.setHint("Nueva Dirección");
        layout.addView(etNewAddress);



        builder.setView(layout)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String newAddress = etNewAddress.getText().toString();
                    updateAddress(newAddress);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showChangeUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cambiar Nombre de Usuario");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        EditText etNewUsername = new EditText(getActivity());
        etNewUsername.setHint("Nuevo Nombre");
        layout.addView(etNewUsername);

        builder.setView(layout)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String newUsername = etNewUsername.getText().toString();
                    updateUsername(newUsername);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private boolean isValidEmail(@NonNull String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void updateEmail(@NonNull String newEmail) {
        SharedPreferences prefs = getActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", "");

        Map<String, String> body = new HashMap<>();
        body.put("email", newEmail);

        apiService.changeEmail("Bearer " + accessToken, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Correo electrónico actualizado con éxito", Toast.LENGTH_SHORT).show();
                    loadUserData();
                } else {
                    Toast.makeText(getActivity(), "Error al actualizar el correo electrónico", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAddress(@NonNull String newAddress) {
        SharedPreferences prefs = getActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", "");

        Map<String, String> body = new HashMap<>();
        body.put("direccion", newAddress);

        apiService.changeAddress("Bearer " + accessToken, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Dirección actualizada con éxito", Toast.LENGTH_SHORT).show();
                    loadUserData();
                } else {
                    Toast.makeText(getActivity(), "Error al actualizar la dirección", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUsername(@NonNull String newUsername) {
        SharedPreferences prefs = getActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", "");

        Map<String, String> body = new HashMap<>();
        body.put("username", newUsername);

        apiService.changeUsername("Bearer " + accessToken, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Nombre de usuario actualizado con éxito", Toast.LENGTH_SHORT).show();
                    loadUserData();
                } else {
                    Toast.makeText(getActivity(), "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
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
        String accessToken = prefs.getString("access_token", "");

        apiService.deleteAccount("Bearer " + accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Cuenta eliminada con éxito", Toast.LENGTH_SHORT).show();
                    logoutUser();
                } else {
                    Toast.makeText(getActivity(), "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutUser() {
        SharedPreferences prefs = getActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", "");

        apiService.logout("Bearer " + accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    prefs.edit().clear().apply();
                    Toast.makeText(getActivity(), "Sesión cerrada con éxito", Toast.LENGTH_SHORT).show();

                    NavController navController = Navigation.findNavController(getView());
                    navController.navigate(R.id.action_profileFragment_to_loginFragment);

                } else {
                    Toast.makeText(getActivity(), "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
