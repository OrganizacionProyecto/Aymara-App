package com.example.aymara_app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa el NavHostFragment y el NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Configura la barra de navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Seleccionar fragmento según el ítem clickeado
            switch (item.getItemId()) {
                case R.id.nav_home:
                    navController.navigate(R.id.HomeFragment);
                    return true;
                case R.id.nav_products:
                    navController.navigate(R.id.ProductFragment);
                    return true;
                case R.id.nav_contac:
                    navController.navigate(R.id.ContacFragment);
                    return true;
                case R.id.nav_register:
                    navController.navigate(R.id.registerFragment);
                    return true;
                case R.id.nav_user:
                    navController.navigate(R.id.loginFragment);
                    return true;
            }
            return false;
        });
    }
}
