package com.example.aymara_app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment; // Importa esto
import androidx.navigation.Navigation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController(); // Obtén el NavController desde el NavHostFragment

        // Barra de navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Vincular el NavController con el BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                    case R.id.nav_user:
                        navController.navigate(R.id.loginFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}