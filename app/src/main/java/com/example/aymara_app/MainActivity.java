package com.example.aymara_app;
import com.example.aymara_app.R;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.example.aymara_app.HomeFragment;
//import com.example.aymara_app.ProductFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa el fragmento por defecto
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameContainer, new HomeFragment())
                    .commit();
        }

        // barra de navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // Seleccionar fragmento según el ítem clickeado
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;
                    //case R.id.nav_products:
                     //   selectedFragment = new ProductFragment();
                      //  break;
                    //    case R.id.nav_contact:
                    //       selectedFragment = new ContactFragment();
                    //     break;
                    // case R.id.nav_user:
                    //   selectedFragment = new ProfileFragment();
                    // break;
                }

                // Reemplaza el fragmento actual con el seleccionado
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameContainer, selectedFragment)
                        .addToBackStack(null)
                        .commit();

                return true;
            }
        });
    }
}