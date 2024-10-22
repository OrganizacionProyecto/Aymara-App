package com.example.aymara_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.example.aymara_app.HomeFragment;
//import com.example.aymara_app.ProductFragment;
import com.example.aymara_app.ContacFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private View banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        banner = findViewById(R.id.frame_baner);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> navigateToFragment(item.getItemId(), navController));

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.HomeFragment) {
                banner.setVisibility(View.GONE);
            } else {
                banner.setVisibility(View.VISIBLE);
            }
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
        return prefs.getBoolean("is_logged_in", false);
    }
}