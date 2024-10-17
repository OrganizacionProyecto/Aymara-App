package com.example.aymara_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> navigateToFragment(item.getItemId(), navController));
    }

    private boolean navigateToFragment(int itemId, NavController navController) {
        switch (itemId) {
            case R.id.nav_home:
                navController.navigate(R.id.HomeFragment);
                return true;
            case R.id.nav_products:
                navController.navigate(R.id.ProductFragment);
                return true;
            case R.id.nav_contac: // Corregido aquí
                navController.navigate(R.id.ContacFragment);
                return true;
            case R.id.nav_user:
                if (isUserLoggedIn()) {
                    navController.navigate(R.id.profileFragment);
                } else {
                    navController.navigate(R.id.loginFragment);
                }
                return true;
        }
        return false;
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("AymaraPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false); // Retorna true si está logueado
    }
}
