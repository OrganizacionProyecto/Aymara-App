package com.example.aymara_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

/** Clase para la respuesta del registro **/
class RegisterResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

/** Fragmento de registro **/
public class RegisterFragment extends Fragment {
    private EditText editTextEmail, editTextPassword, editTextUsername;
    private TextView loginText;
    private Button buttonRegister;
    private Retrofit retrofit;
    private AuthService authService;

    /** Constructor vacío necesario **/
    public RegisterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /** Inflar el layout del fragmento **/
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        /** Inicializar los elementos de la interfaz de usuario **/
        loginText = view.findViewById(R.id.loginText);
        editTextUsername = view.findViewById(R.id.username);
        editTextPassword = view.findViewById(R.id.password);
        editTextEmail = view.findViewById(R.id.email);
        buttonRegister = view.findViewById(R.id.registerButton);

        /** Configurar Retrofit **/
        retrofit = new Retrofit.Builder()
                .baseUrl("https://aymara.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authService = retrofit.create(AuthService.class);

        /** Configurar el botón de registro **/
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return view;
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String username = editTextUsername.getText().toString();  // Corregido

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        /** Llamada al servicio de autenticación **/
        Call<RegisterResponse> call = authService.registerUser(new RegisterRequest(email, password));

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error en el registro: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}















