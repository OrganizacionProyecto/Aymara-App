package com.example.aymara_app;

<<<<<<< HEAD
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

=======

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import com.example.aymara_app.profile.LoginFragment;


public class MainActivity extends AppCompatActivity {
>>>>>>> origin/CeciCogot
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD
    }
}
=======

        // Cargar el fragmento de login al iniciar la actividad
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .commit();
    }
}
>>>>>>> origin/CeciCogot
