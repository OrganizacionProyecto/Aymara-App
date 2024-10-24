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
import com.example.aymara_app.network.ApiService;
import com.example.aymara_app.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterFragment extends Fragment {
    private EditText editTextEmail, editTextPassword, editTextUsername, editTextFirstName, editTextLastName;
    private TextView loginText;
    private Button buttonRegister;
    private Retrofit retrofit;
    private ApiService apiService;

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
        editTextFirstName = view.findViewById(R.id.firstname);
        editTextLastName = view.findViewById(R.id.lastname);
        buttonRegister = view.findViewById(R.id.registerButton);

        /** Configurar Retrofit utilizando ApiClient **/
        retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

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
        String username = editTextUsername.getText().toString();
        String firstname = editTextFirstName.getText().toString();
        String lastname = editTextLastName.getText().toString();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || firstname.isEmpty() || lastname.isEmpty()) {
            Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        /** Llamada al servicio de autenticación **/
        RegisterRequest registerRequest = new RegisterRequest(email, password, username, firstname, lastname);
        Call<RegisterResponse> call = apiService.registerUser(registerRequest);

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
