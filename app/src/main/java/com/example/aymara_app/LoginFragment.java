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

public class LoginFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);
        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView);

        // Configurar listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Validación simple de entrada
                if (email.isEmpty()) {
                    emailEditText.setError("Por favor, ingresa tu correo electrónico");
                    return;
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Por favor, ingresa tu contraseña");
                    return;
                }

                // Lógica de autenticación (esto es solo un ejemplo)
                if (email.equals("usuario@example.com") && password.equals("123456")) {
                    Toast.makeText(getActivity(), "Login exitoso", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Usuario o contraseña incorrectas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementar lógica para recuperar contraseña
                Toast.makeText(getContext(), "Función de recuperar contraseña no implementada", Toast.LENGTH_SHORT).show();
            }
        });
        // Aquí puedes agregar cualquier configuración adicional para passwordEditText
        passwordEditText.setHint("Introduce tu contraseña");
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aquí deberías implementar tu lógica de autenticación
        // Por ejemplo, llamar a un método de tu ViewModel o Repository

        // Este es solo un ejemplo, reemplaza con tu lógica real
        if (email.equals("usuario@ejemplo.com") && password.equals("contraseña123")) {
            Toast.makeText(getContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            // Navegar a la siguiente pantalla o realizar otras acciones necesarias
        } else {
            Toast.makeText(getContext(), "Usuario y/o contraseña incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
}