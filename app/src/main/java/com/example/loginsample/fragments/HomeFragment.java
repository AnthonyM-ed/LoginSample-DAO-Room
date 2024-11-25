package com.example.loginsample.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.loginsample.R;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Configura los botones o elementos que respondan a los clicks
        Button edificacionesButton = view.findViewById(R.id.button_edificaciones);
        Button mapaButton = view.findViewById(R.id.button_mapa);

        // Asigna los métodos de clic
        edificacionesButton.setOnClickListener(this::onEdificacionesClick);
        mapaButton.setOnClickListener(this::onMapClick);

        return view;
    }

    public void onEdificacionesClick(View view) {
        ListaFragments edificacionesFragment = new ListaFragments();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, edificacionesFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onMapClick(View view) {
        MapaFragment mapaFragment = new MapaFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, mapaFragment)
                .addToBackStack(null)
                .commit();
    }
}
