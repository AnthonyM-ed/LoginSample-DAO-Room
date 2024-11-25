package com.example.loginsample.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.loginsample.R;

public class DetailFragment extends Fragment {
    private String title;
    private String description;
    private int imageResId;  // Cambiar a String para manejar el nombre de la imagen

    // Método para crear una nueva instancia del fragmento con parámetros
    public static DetailFragment newInstance(String title, String description, int imageName) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("description", description);
        args.putInt("imageName", imageName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView titleTextView = view.findViewById(R.id.title_text_view);
        TextView descriptionTextView = view.findViewById(R.id.description_text_view);
        ImageView imageView = view.findViewById(R.id.image_view);
        Button btnViewMansion = view.findViewById(R.id.btn_view_mansion);
        Button btnViewComments = view.findViewById(R.id.btn_view_comments);

        // Configurar los valores de los elementos de la interfaz
        titleTextView.setText(title);
        descriptionTextView.setText(description);

        // Cargar la imagen utilizando el ID del recurso de la imagen
        imageView.setImageResource(imageResId);

        // Redirigir al fragmento MansionFragment
        btnViewMansion.setOnClickListener(v -> {
            MansionFragment mansionFragment = new MansionFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, mansionFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Botón para redirigir al fragmento CommentsSectionFragment
        btnViewComments.setOnClickListener(v -> {
            CommentsSectionFragment commentsFragment = new CommentsSectionFragment();

            // Pasar solo el nombre del edificio al fragmento de comentarios
            Bundle args = new Bundle();
            args.putString("buildingName", title);  // Pasar el nombre del edificio
            commentsFragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, commentsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            description = getArguments().getString("description");
            imageResId  = getArguments().getInt("imageName");  // Recuperar el ID del recurso de la imagen
        }
    }
}
