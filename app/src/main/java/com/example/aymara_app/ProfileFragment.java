
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
import android.text.InputFilter;
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
    private SharedPreferences prefs;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

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
        prefs = getActivity().getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);

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


        InputFilter[] usernameFilters = new InputFilter[2];
        usernameFilters[0] = new InputFilter.LengthFilter(15);
        usernameFilters[1] = (source, start, end, dest, dstart, dend) -> {
            if (dest.length() < 3 && (dest.length() + (end - start) > 15)) {
                Toast.makeText(getActivity(), "El nombre de usuario debe tener al menos 3 caracteres y no exceder 15 caracteres", Toast.LENGTH_SHORT).show();
                return "";
            }
            return null;
        };
        etUsername.setFilters(usernameFilters);


        InputFilter[] addressFilters = new InputFilter[1];
        addressFilters[0] = new InputFilter.LengthFilter(50);
        etDireccion.setFilters(addressFilters);

        return view;
    }

    private void loadUserData() {
        String accessToken = prefs.getString("access_token", "");

        apiService.getUserDetails("Bearer " + accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try (ResponseBody responseBody = response.body()) {
                        String responseString = responseBody.string();
                        JSONObject jsonObject = new JSONObject(responseString);

                        etUsername.setText(jsonObject.getString("username"));
                        etFirst_Name.setText(jsonObject.getString("first_name"));
                        etLast_Name.setText(jsonObject.getString("last_name"));
                        etEmail.setText(jsonObject.getString("email"));
                        etDireccion.setText(jsonObject.getString("direccion"));

                        TextView tvProfileTitle = getView().findViewById(R.id.tvProfileTitle);
                        String username = jsonObject.getString("username");
                        tvProfileTitle.setText("Perfil de " + username);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleErrorResponse(Response<ResponseBody> response) {
        switch (response.code()) {
            case 401:
                Toast.makeText(getActivity(), "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_SHORT).show();
                refreshToken();
                break;
            case 404:
                Toast.makeText(getActivity(), "Usuario no encontrado.", Toast.LENGTH_SHORT).show();
                break;
            case 403:
                Toast.makeText(getActivity(), "Acceso denegado. No tienes permiso para ver estos datos.", Toast.LENGTH_SHORT).show();
                break;
            case 500:
                Toast.makeText(getActivity(), "Error interno del servidor. Por favor, inténtelo más tarde.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), "Error al cargar los datos del usuario. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void refreshToken() {
        String refreshToken = prefs.getString("refresh_token", "");
        if (!refreshToken.isEmpty()) {
            apiService.refreshToken(refreshToken).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseString = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseString);
                            String newAccessToken = jsonObject.getString("access_token");
                            String newRefreshToken = jsonObject.getString("refresh_token");

                            prefs.edit()
                                    .putString("access_token", newAccessToken)
                                    .putString("refresh_token", newRefreshToken)
                                    .apply();

                            loadUserData(); // Recargar datos con el nuevo token
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            handleRefreshTokenError();
                        }
                    } else {
                        handleRefreshTokenError();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    handleRefreshTokenError();
                }
            });
        } else {
            handleRefreshTokenError();
        }
    }

    private void handleRefreshTokenError() {
        // Limpiar las preferencias
        prefs.edit().clear().apply();

        // Mostrar mensaje al usuario
        Toast.makeText(getActivity(), "Su sesión ha expirado. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show();

        // Navegar al login
        if (getActivity() != null && isAdded()) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.loginFragment);
        }
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            Toast.makeText(getContext(), "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            Toast.makeText(getContext(), "La contraseña debe contener al menos una letra mayúscula", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.matches(".*[0-9].*")) {
            Toast.makeText(getContext(), "La contraseña debe contener al menos un número", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.matches(".*[a-zA-Z].*")) {
            Toast.makeText(getContext(), "La contraseña debe contener al menos una letra (mayúscula o minúscula)", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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

        EditText etConfirmPassword = new EditText(getActivity());
        etConfirmPassword.setHint("Confirmar Contraseña");
        etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etConfirmPassword);

        builder.setView(layout).setPositiveButton("Confirmar", (dialog, which) -> {
                    String oldPassword = etOldPassword.getText().toString();
                    String newPassword = etNewPassword.getText().toString();
                    String confirmPassword = etConfirmPassword.getText().toString();

                    if (newPassword.isEmpty() || oldPassword.isEmpty() || confirmPassword.isEmpty()) {
                        Toast.makeText(getActivity(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                    } else if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(getActivity(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    } else if (!isValidPassword(newPassword)){
                        Toast.makeText(getActivity(), "La contraseña debe tener al menos 8 caracteres e incluir al menos una letra y un número", Toast.LENGTH_SHORT).show();
                    } else {
                        updatePassword(oldPassword, newPassword);
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
                    String newAddress = etNewAddress.getText().toString().trim();

                    if (newAddress.isEmpty()) {
                        Toast.makeText(getActivity(), "El domicilio no puede estar vacío", Toast.LENGTH_SHORT).show();
                    } else if (newAddress.length() < 3) {
                        Toast.makeText(getActivity(), "El domicilio debe tener al menos 3 caracteres", Toast.LENGTH_SHORT).show();
                    } else {
                        updateAddress(newAddress);
                    }
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
                    String newUsername = etNewUsername.getText().toString().trim();

                    if (newUsername.isEmpty()) {
                        Toast.makeText(getActivity(), "El nombre de usuario no puede estar vacío", Toast.LENGTH_SHORT).show();
                    } else if (newUsername.length() < 3) {
                        Toast.makeText(getActivity(), "El nombre de usuario debe tener al menos 3 caracteres", Toast.LENGTH_SHORT).show();
                    } else {

                        updateUsername(newUsername);
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
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
                    handleErrorResponse(response);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    handleErrorResponse(response);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
        String accessToken = prefs.getString("access_token", "");

        apiService.deleteAccount("Bearer " + accessToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Cuenta eliminada con éxito", Toast.LENGTH_SHORT).show();
                    prefs.edit().clear().apply();
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_profileFragment_to_loginFragment);
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(getActivity(), "Sesión cerrada.", Toast.LENGTH_SHORT).show();
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.loginFragment);
    }
}
