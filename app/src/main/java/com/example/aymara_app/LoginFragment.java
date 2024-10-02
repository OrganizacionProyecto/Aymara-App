package com.example.aymara_app;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;
    private ImageView passwordToggle;
    private boolean isPasswordVisible = false;

    private OkHttpClient client = new OkHttpClient();
    private static final String LOGIN_URL = "https://tu-api-fake.com/api/login"; // Cambia esta URL a la de tu API fake o real

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);
        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView);
        passwordToggle = view.findViewById(R.id.passwordToggle);

        forgotPasswordTextView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!isValidEmail(email)) {
                emailEditText.setError("Por favor, ingresa un correo electrónico válido");
                return;
            }

            if (!isValidPassword(password)) {
                passwordEditText.setError("La contraseña debe tener al menos 6 caracteres e incluir al menos una letra y un número");
                return;
            }

            // Realizar el login con el backend
            loginWithJWT(email, password);
        });

        passwordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.icon_invisiblec);
                isPasswordVisible = false;
            } else {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.icon_visiblea);
                isPasswordVisible = true;
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });
    }

    private void loginWithJWT(String email, String password) {
        // Crea el objeto JSON con las credenciales
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Crea la solicitud
        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        // Enviar la solicitud
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Manejar la respuesta exitosa
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String accessToken = jsonResponse.getString("access");
                        String refreshToken = jsonResponse.getString("refresh");

                        // Almacenar los tokens
                        storeTokens(accessToken, refreshToken);

                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            // Aquí puedes navegar a otra pantalla o realizar otras acciones
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void storeTokens(String accessToken, String refreshToken) {
        // Almacenar los tokens en SharedPreferences
        Context context = getActivity();
        if (context != null) {
            context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("accessToken", accessToken)
                    .putString("refreshToken", refreshToken)
                    .apply();
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*[a-zA-Z].*") && password.matches(".*\\d.*");
    }
}
