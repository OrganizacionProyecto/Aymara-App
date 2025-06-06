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
import com.example.aymara_app.network.ApiService;
import com.example.aymara_app.network.ApiClient;
import com.example.aymara_app.RegisterRequest;
import com.example.aymara_app.RegisterResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterFragment extends Fragment {
    private EditText editTextEmail, editTextPassword, editTextConfirPass, editTextUsername, editTextFirstName, editTextLastName;
    private EditText editTextDireccion;
    private TextView loginText;
    private Button buttonRegister;
    private Retrofit retrofit;
    private ApiService apiService;

    /* Constructor vacío necesario */
    public RegisterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* Inflar el layout del fragmento */
        View view = inflater.inflate (R.layout.fragment_register, container, false);

        /* Inicializar los elementos de la interfaz de usuario */
        loginText = view.findViewById (R.id.loginText);
        editTextUsername = view.findViewById (R.id.username);
        editTextPassword = view.findViewById (R.id.password);
        editTextConfirPass = view.findViewById (R.id.confirpass);
        editTextEmail = view.findViewById (R.id.email);
        editTextFirstName = view.findViewById (R.id.firstname);
        editTextLastName = view.findViewById (R.id.lastname);
        editTextDireccion = view.findViewById(R.id.direccion);

        buttonRegister = view.findViewById (R.id.registerButton);

        /* Configurar Retrofit utilizando ApiClient */
        retrofit = ApiClient.getClient ();
        apiService = retrofit.create (ApiService.class);

        /* Configurar el botón de registro */
        buttonRegister.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                registerUser ();
            }
        });


        if (loginText != null) {
            loginText.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_registerFragment_to_loginFragment);
            });
        }

        return view;
    }

    private void registerUser() {
        String email = editTextEmail.getText ().toString ();
        String password = editTextPassword.getText ().toString ();
        String confirPass = editTextConfirPass.getText ().toString ();
        String username = editTextUsername.getText ().toString ();
        String firstname = editTextFirstName.getText ().toString ();
        String lastname = editTextLastName.getText ().toString ();
        String direccion = editTextDireccion.getText().toString();


        /*Validaciones*/
        if (email.isEmpty() || password.isEmpty() || confirPass.isEmpty() || username.isEmpty() ||
                firstname.isEmpty() || lastname.isEmpty() || direccion.isEmpty()) {
            Toast.makeText (getContext (), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show ();
            return;
        }

        if (!password.equals (confirPass)) {
            Toast.makeText (getContext (), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show ();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(getContext(), "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            Toast.makeText(getContext(), "La contraseña debe contener al menos una letra mayúscula", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!password.matches(".*[0-9].*")) {
            Toast.makeText(getContext(), "La contraseña debe contener al menos un número", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!password.matches(".*[a-zA-Z].*")) {
            Toast.makeText(getContext(), "La contraseña debe contener al menos una letra (mayúscula o minúscula)", Toast.LENGTH_SHORT).show();
            return;
        }

        /* Llamada al servicio de autenticación */
        RegisterRequest registerRequest = new RegisterRequest (email, password, username, firstname, lastname);
        registerRequest.setDireccion(direccion);
        registerRequest.setPassword2(confirPass);

        Call<RegisterResponse> call = apiService.registerUser (registerRequest);

        call.enqueue (new Callback<RegisterResponse> () {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful ()) {
                    Toast.makeText (getContext (), "Registro exitoso. Por favor, inicia sesión.", Toast.LENGTH_LONG).show ();

                    NavController navController = Navigation.findNavController(getView());
                    navController.navigate(R.id.action_registerFragment_to_loginFragment);

                } else {

                    String errorMessage = "Error en el registro.";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();

                            if (errorBody.contains("email") && errorBody.contains("actualmente en uso")) {
                                errorMessage = "El email ya está registrado.";
                            } else if (errorBody.contains("username") && errorBody.contains("ya existe")) {
                                errorMessage = "El nombre de usuario ya existe.";
                            } else {
                                errorMessage = "Error en el registro: " + response.message();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorMessage = "Error al procesar la respuesta del servidor.";
                    }
                    Toast.makeText (getContext (), errorMessage, Toast.LENGTH_LONG).show ();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText (getContext (), "Fallo en la conexión: " + t.getMessage (), Toast.LENGTH_SHORT).show ();
            }
        });
    }
}