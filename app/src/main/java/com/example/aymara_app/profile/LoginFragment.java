package com.example.aymara_app.profile;

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

import com.example.aymara_app.R;

public class LoginFragment extends Fragment {

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView forgotPassword;
    private TextView registerText;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Inicializar vistas
        emailInput = view.findViewById(R.id.emailEditText);
        passwordInput = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);
        forgotPassword = view.findViewById(R.id.forgotPasswordTextView);

        // Configurar evento de clic en el botón de login
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Aquí puedes agregar la lógica para autenticar al usuario
                Toast.makeText(getActivity(), "Login exitoso", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar evento de clic en "¿Olvidaste tu contraseña?"
        forgotPassword.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Redirigiendo a la página de recuperación", Toast.LENGTH_SHORT).show();
        });

        // Configurar evento de clic en "¿No tienes cuenta? Regístrate"
        registerText.setOnClickListener(v -> {
            // Aquí puedes implementar la lógica para redirigir al fragmento o actividad de registro
            Toast.makeText(getActivity(), "Redirigiendo a la página de registro", Toast.LENGTH_SHORT).show();


        });

        return view;
    }
}
