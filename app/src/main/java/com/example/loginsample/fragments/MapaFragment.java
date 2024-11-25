package com.example.loginsample.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginsample.R;
import com.example.loginsample.model.database.AppDatabase;
import com.example.loginsample.model.dao.EdificioDao;
import com.example.loginsample.model.ent.EdificioEntity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapaFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SearchView searchView;

    private EdificioDao edificioDao;

    public MapaFragment() {
        // Constructor vacío necesario
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos el DAO para interactuar con la base de datos
        edificioDao = AppDatabase.getInstance(getActivity()).edificioDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        // Inicializa el SupportMapFragment y notifica cuando el mapa está listo
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Inicializa el SearchView
        searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Manejar la búsqueda de ubicación
                buscarUbicacion(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Configura el adaptador personalizado de la ventana de información
        mMap.setInfoWindowAdapter(new ventanaInformacion(getLayoutInflater()));

        // Cargar los edificios desde la base de datos
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Obtener los edificios desde la base de datos
                List<EdificioEntity> edificioList = edificioDao.getAllEdificios();

                // Itera sobre los edificios obtenidos y agregar marcadores al mapa
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (EdificioEntity edificio : edificioList) {
                            // Obtiene la latitud y longitud de cada edificio directamente
                            double lat = edificio.getEdiLatitud(); // Asegúrate de tener estos métodos en tu entidad
                            double lng = edificio.getEdiLongitud();

                            LatLng latLng = new LatLng(lat, lng);

                            // Agregar marcador en el mapa
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(edificio.getEdiName())
                                    .snippet(edificio.getDescription()));
                        }

                        // Centra la cámara en una ubicación general de Arequipa
                        LatLng arequipaCenter = new LatLng(-16.4090474, -71.537451);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arequipaCenter, 13));
                    }
                });
            }
        }).start();
    }

    private void buscarUbicacion(String location) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.clear(); // Limpia el mapa de marcadores anteriores
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)); // Ajusta el zoom según sea necesario
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error al buscar la ubicación", Toast.LENGTH_SHORT).show();
        }
    }

    public class ventanaInformacion implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;

        public ventanaInformacion(LayoutInflater inflater) {
            // Infla el layout personalizado de la ventana de información
            mWindow = inflater.inflate(R.layout.custom_info_window, null);
        }

        private void renderWindowText(Marker marker, View view) {
            TextView title = view.findViewById(R.id.title);
            TextView description = view.findViewById(R.id.description);

            title.setText(marker.getTitle());
            description.setText(marker.getSnippet());
        }

        @Override
        public View getInfoWindow(Marker marker) {
            renderWindowText(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
}
