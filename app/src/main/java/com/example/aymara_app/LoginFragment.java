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
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!isValidEmail(email)) {
                emailEditText.setError("Por favor, ingresa un correo electrónico válido");
                return;
            }

            if (!isValidPassword(password)) {
                passwordEditText.setError("La contraseña debe tener al menos 8 caracteres e incluir al menos una letra y un número");
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
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
        return password.length() >= 8 && password.matches(".*\\d.*");
    }

    // Crea un OkHttpClient inseguro que omite la validación de certificados SSL (para desarrollo)
    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Crea un administrador de confianza que acepte cualquier certificado SSL
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

            // Instala el administrador de confianza "ingenuo"
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Crea un socket de fábrica que usa nuestro administrador de confianza
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

            // Omite la verificación del nombre del host
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
