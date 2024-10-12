package com.example.aymara_app;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aymara_app.R;


public class RegisterFragment extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginButton;

    /**
     * VER: este METODO CUANDO PASEMOS A PROYECTO public View onCreateView
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        /**Detector de clic en boton de iniciar sesion**/
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("usuario") && password.getText().toString().equals("1234")) {
                    Toast.makeText(contex: RegisterFragment.this, "Inicio Sesión Exitoso", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(contex: RegisterFragment.this, "Error Inicio Sesión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
