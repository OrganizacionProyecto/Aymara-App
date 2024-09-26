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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class LoginFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
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
                attemptLogin();
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega al fragmento RegisterFragment o al fragmento de recuperación de contraseña
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_loginFragment_to_registerFragment); // Cambia a la acción correcta
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
        // Este es solo un ejemplo, reemplaza con tu lógica real
        if (email.equals("usuario@ejemplo.com") && password.equals("contraseña123")) {
            Toast.makeText(getContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            // Navegar a la siguiente pantalla o realizar otras acciones necesarias
        } else {
            Toast.makeText(getContext(), "Usuario y/o contraseña incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
}