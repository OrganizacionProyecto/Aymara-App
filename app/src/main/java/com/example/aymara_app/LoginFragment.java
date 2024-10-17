package com.example.aymara_app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    private OkHttpClient client;
    private static final String LOGIN_URL = "https://aymara.pythonanywhere.com/api/auth/login/";

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

        client = getUnsafeOkHttpClient();

        forgotPasswordTextView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (validateInputs(email, password)) {
                loginWithJWT(email, password);
            }
        });

        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
    }

    private boolean validateInputs(String email, String password) {
        if (!isValidEmail(email)) {
            emailEditText.setError("Por favor, ingresa un correo electrónico válido");
            return false;
        }
        if (!isValidPassword(password)) {
            passwordEditText.setError("La contraseña debe tener al menos 8 caracteres e incluir al menos una letra y un número");
            return false;
        }
        return true;
    }

    private void togglePasswordVisibility() {
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
    }

    private void storeTokens(String accessToken, String refreshToken) {
        Context context = getActivity();
        if (context != null) {
            context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("is_logged_in", true)
                    .putString("access_token", accessToken)
                    .putString("refresh_token", refreshToken)
                    .apply();
        }
    }

    private void loginWithJWT(String email, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("Error de conexión: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    handleLoginSuccess(response.body().string());
                } else {
                    showToast("Credenciales incorrectas");
                }
            }
        });
    }

    private void handleLoginSuccess(String responseData) {
        try {
            JSONObject jsonResponse = new JSONObject(responseData);
            String accessToken = jsonResponse.getString("access");
            String refreshToken = jsonResponse.getString("refresh");
            storeTokens(accessToken, refreshToken);
            fetchUserProfile(accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchUserProfile(String accessToken) {
        String userProfileUrl = "https://aymara.pythonanywhere.com/api/auth/user/";
        Request request = new Request.Builder()
                .url(userProfileUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("Error al obtener perfil: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    handleUserProfileResponse(response.body().string());
                } else {
                    showToast("Error al obtener perfil");
                }
            }
        });
    }

    private void handleUserProfileResponse(String userProfileData) {
        try {
            JSONObject jsonUserProfile = new JSONObject(userProfileData);
            String email = jsonUserProfile.getString("email");

            // Guardar perfil en SharedPreferences
            Context context = getActivity();
            if (context != null) {
                context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("user_email", email)
                        .apply();
            }

            // Navegar al fragmento de perfil
            getActivity().runOnUiThread(() -> {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString("email", email);
                navController.navigate(R.id.action_loginFragment_to_profileFragment, bundle);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches(".*\\d.*");
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
