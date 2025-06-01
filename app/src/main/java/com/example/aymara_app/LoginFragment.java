package com.example.aymara_app;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.concurrent.TimeUnit;

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
    private static final String LOGIN_URL = "https://aymara.pythonanywhere.com/api/auth/token/";
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_MINUTES = 5;

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
                if (isAccountLocked()) {
                    showToast("Cuenta bloqueada. Intenta de nuevo más tarde.");
                } else {
                    loginWithJWT(email, password);
                }
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
                    resetFailedAttempts();
                    handleLoginSuccess(response.body().string());
                } else {
                    if (response.code() == 401) {
                        showToast("Usuario o contraseña incorrectos.");
                    } else if (response.code() == 404) {
                        showToast("Usuario no encontrado. Verifica tus credenciales.");
                    } else {
                        showToast("Error al iniciar sesión: " + response.message());
                    }
                    handleFailedLogin();
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
        String userProfileUrl = "https://aymara.pythonanywhere.com/api/users/me/";
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

            Context context = getActivity();
            if (context != null) {
                context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("user_email", email)
                        .apply();
            }

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

    private void handleFailedLogin() {
        Context context = getActivity();
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
            int failedAttempts = preferences.getInt("failed_attempts", 0) + 1;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("failed_attempts", failedAttempts);
            editor.apply();

            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                lockAccount();
            } else {
                showToast("Credenciales incorrectas. Número de intentos fallidos: " + failedAttempts);
            }
        }
    }

    private void lockAccount() {
        Context context = getActivity();
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            long lockTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(LOCK_TIME_MINUTES);
            editor.putLong("lock_time", lockTime);
            editor.apply();
            showToast("Cuenta bloqueada temporalmente por múltiples intentos fallidos.");
        }
    }

    private boolean isAccountLocked() {
        Context context = getActivity();
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
            long lockTime = preferences.getLong("lock_time", 0);
            if (lockTime > System.currentTimeMillis()) {
                return true;
            }
        }
        return false;
    }

    private void resetFailedAttempts() {
        Context context = getActivity();
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
            preferences.edit().putInt("failed_attempts", 0).apply();
        }
    }

    private void showToast(final String message) {
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show());
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches(".*[A-Za-z].*") && password.matches(".*[0-9].*");
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
