package com.example.loginsample.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginsample.EdificioAdapter;  // Cambiado a EdificioAdapter
import com.example.loginsample.R;
import com.example.loginsample.model.database.AppDatabase;
import com.example.loginsample.model.ent.EdificioEntity;  // Cambiado a EdificioEntity
import com.example.loginsample.model.dao.EdificioDao;
import java.util.ArrayList;
import java.util.List;

public class ListaFragments extends Fragment {
    private RecyclerView recyclerView;
    private EdificioAdapter edificioAdapter;
    private List<EdificioEntity> edificioList;
    private List<EdificioEntity> filteredEdificioList;
    private EditText searchBar;
    private EdificioDao edificioDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos el DAO para interactuar con la base de datos
        edificioDao = AppDatabase.getInstance(getActivity()).edificioDao();
    }

    private class GetEdificiosTask extends AsyncTask<Void, Void, List<EdificioEntity>> {
        @Override
        protected List<EdificioEntity> doInBackground(Void... voids) {
            return edificioDao.getAllEdificios();
        }

        @Override
        protected void onPostExecute(List<EdificioEntity> result) {
            super.onPostExecute(result);
            // Aquí actualizas el UI con los datos obtenidos
            edificioList = result;
            filteredEdificioList.addAll(edificioList);
            edificioAdapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista, container, false);

        // Configuración del RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        // Inicialización de listas
        edificioList = new ArrayList<>();  // Esta lista debe ser llenada con datos de la base de datos
        filteredEdificioList = new ArrayList<>();  // Se hace una copia

        // Ejecutamos el AsyncTask para cargar los edificios
        new GetEdificiosTask().execute();

        // Configuración del adaptador
        edificioAdapter = new EdificioAdapter(filteredEdificioList, edificio -> {
            // Crear el fragmento con los detalles del edificio
            DetailFragment detailFragment = DetailFragment.newInstance(
                    edificio.getEdiName(),
                    edificio.getDescription(),
                    edificio.getEdiImagen(),
                    edificio.getEdAudId()
            );

            // Manejo de la transacción del fragmento
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, detailFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        recyclerView.setAdapter(edificioAdapter);

        // Configuración del campo de búsqueda
        searchBar = view.findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    // Método de filtro
    private void filter(String text) {
        filteredEdificioList.clear();
        if (text.isEmpty()) {
            filteredEdificioList.addAll(edificioList);
        } else {
            for (EdificioEntity edificio : edificioList) {
                if (edificio.getEdiName().toLowerCase().contains(text.toLowerCase())) {
                    filteredEdificioList.add(edificio);
                }
            }
        }
        edificioAdapter.notifyDataSetChanged();
    }
}
