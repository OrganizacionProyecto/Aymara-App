package com.example.aymara_app;

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
import androidx.navigation.fragment.NavHostFragment;


public class LoginFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;
    private ImageView passwordToggle;
    private boolean isPasswordVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        passwordToggle = view.findViewById(R.id.passwordToggle);

        // Configurar el listener para navegar al RegisterFragment
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Obtener el NavController y navegar al RegisterFragment
                NavController navController = NavHostFragment.findNavController(LoginFragment.this);
                navController.navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });

        // Lógica de autenticación (esto es solo un ejemplo)
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (email.isEmpty()) {
                    emailEditText.setError("Por favor, ingresa tu correo electrónico");
                    return;
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Por favor, ingresa tu contraseña");
                    return;
                }

                if (email.equals("usuario@example.com") && password.equals("123456")) {
                    Toast.makeText(getActivity(), "Login exitoso", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "Usuario o contraseña incorrectas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Listener para mostrar/ocultar la contraseña
        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }
}
