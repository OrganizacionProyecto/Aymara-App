package com.example.aymara_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Encontrar el TextView de la dirección
        TextView addressTextView = view.findViewById(R.id.addressTextView);

        // Agregar un listener de clic para abrir Google Maps
        addressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMaps();
            }
        });

        return view;
    }

    // Método para abrir Google Maps con la dirección
    private void openMaps() {
        // Dirección a buscar en Google Maps
        String address = "Av. San Martin 50, Colonia Caroya, Argentina";
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));

        // Crear el intent de Google Maps
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Verificar que Google Maps esté disponible y empezar la actividad
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getContext(), "Google Maps no está disponible", Toast.LENGTH_SHORT).show();
        }
    }
}
