package com.example.aymara_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private View banner;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        banner = findViewById(R.id.frame_baner);
        bottomNavigationView = findViewById(R.id.bottom_navigation); // Initialize here

        TextView sitioWeb = findViewById(R.id.tv_sitio_web);
        if (sitioWeb != null) {
            sitioWeb.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://aymara.netlify.app/"));
                startActivity(intent);
            });
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        updateCartIconVisibility();

        // Listener for item selection in the BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> navigateToFragment(item.getItemId(), navController));

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.HomeFragment) {
                banner.setVisibility(View.GONE);
            } else {
                banner.setVisibility(View.VISIBLE);
            }

            updateCartIconVisibility();
        });
    }

    private boolean navigateToFragment(int itemId, NavController navController) {
        switch (itemId) {
            case R.id.nav_home:
                navController.navigate(R.id.HomeFragment);
                return true;
            case R.id.nav_products:
                navController.navigate(R.id.ProductFragment);
                return true;
            case R.id.nav_cart:
                if (isUserLoggedIn()) {
                    navController.navigate(R.id.cartFragment);
                }
                return true;
            case R.id.nav_contac:
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

        return !prefs.getString("access_token", "").isEmpty();
    }

    private void updateCartIconVisibility() {
        if (bottomNavigationView != null) {
            boolean isLoggedIn = isUserLoggedIn();
            bottomNavigationView.getMenu().findItem(R.id.nav_cart).setVisible(isLoggedIn);
        }
    }
}