package com.example.loginsample.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginsample.CommentAdapter;
import com.example.loginsample.R;
import com.example.loginsample.model.database.AppDatabase;
import com.example.loginsample.model.database.ComentarioRepository;
import com.example.loginsample.model.database.UserRepository;
import com.example.loginsample.model.ent.ComentarioEntity;
import com.example.loginsample.model.ent.EdificioEntity;

import java.util.ArrayList;
import java.util.List;

public class CommentsSectionFragment extends Fragment {
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private ComentarioRepository comentarioRepository;
    private List<ComentarioEntity> commentList;
    private EditText commentInput;
    private RatingBar ratingBar;
    private RatingBar averageRatingBar;
    private TextView averageRateTextView;
    private int buildingId; // ID del edificio
    private UserRepository userRepository; // Repositorio de usuario

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments_section, container, false);

        AppDatabase database = AppDatabase.getInstance(requireContext());
        // Inicializar repositorios
        comentarioRepository = new ComentarioRepository(database);
        userRepository = new UserRepository(database);

        // Inicializar componentes
        commentInput = view.findViewById(R.id.comment_input);
        ratingBar = view.findViewById(R.id.rating_bar);
        averageRatingBar = view.findViewById(R.id.average_rating_bar);
        averageRateTextView = view.findViewById(R.id.average_rate_text_view);
        Button submitCommentButton = view.findViewById(R.id.submit_comment_button);
        commentsRecyclerView = view.findViewById(R.id.comments_recycler_view);
        Button btnBackToDetail = view.findViewById(R.id.btn_back_to_detail);

        if (getArguments() != null) {
            String buildingName = getArguments().getString("buildingName");

            // Obtener el edificio por nombre de manera asíncrona
            comentarioRepository.getBuildingByName(buildingName, new ComentarioRepository.GetBuildingCallback() {
                @Override
                public void onBuildingLoaded(EdificioEntity edificio) {
                    if (edificio != null) {
                        buildingId = edificio.getIdEdificio(); // Establecer el ID del edificio
                        loadCommentsFromDatabase(); // Ahora podemos cargar los comentarios
                    }
                }
            });
        }

        // Configuración del RecyclerView
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, userRepository); // Pasamos el repositorio al adaptador
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(commentAdapter);

        // Cargar comentarios desde la base de datos
        loadCommentsFromDatabase();

        // Calcular y mostrar el promedio de calificación
        float averageRating = calculateAndRoundAverageRating(commentList);
        averageRatingBar.setRating(averageRating);
        averageRateTextView.setText(String.format("%.1f", averageRating));

        // Funcionalidad para agregar un nuevo comentario
        submitCommentButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString();
            int rating = (int) ratingBar.getRating();

            if (!commentText.isEmpty() && rating > 0) {
                // Obtener el ID del usuario logueado desde SharedPreferences
                SharedPreferences sharedPref = getActivity().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                int userId = sharedPref.getInt("USER_ID", -1); // -1 es el valor por defecto si no se encuentra la clave

                if (userId != -1) { // Verifica que se encontró un ID de usuario válido
                    ComentarioEntity newComment = new ComentarioEntity();
                    newComment.setIdEdificio(buildingId); // Asociar comentario al edificio
                    newComment.setComentario(commentText);
                    newComment.setCalificacion(rating);
                    newComment.setIdUser(userId); // Usar el ID del usuario logueado

                    comentarioRepository.addComment(newComment);
                    commentList.add(newComment);
                    commentAdapter.notifyItemInserted(commentList.size() - 1);

                    // Limpiar campos de entrada
                    commentInput.setText("");
                    ratingBar.setRating(0);

                    // Actualizar promedio
                    float updatedAverageRating = calculateAndRoundAverageRating(commentList);
                    averageRatingBar.setRating(updatedAverageRating);
                    averageRateTextView.setText(String.format("%.1f", updatedAverageRating));
                } else {
                    Toast.makeText(getContext(), "Usuario no encontrado. Por favor, inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Por favor, ingresa un comentario y calificación válida.", Toast.LENGTH_SHORT).show();
            }
        });

        // Regresar al fragmento anterior
        btnBackToDetail.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    // Método para cargar comentarios desde la base de datos usando el callback del repositorio
    private void loadCommentsFromDatabase() {
        comentarioRepository.getCommentsByBuildingId(buildingId, new ComentarioRepository.GetCommentsCallback() {
            @Override
            public void onCommentsLoaded(List<ComentarioEntity> comments) {
                // Actualizamos la lista de comentarios y notificar al adaptador
                commentList.clear();
                commentList.addAll(comments);
                commentAdapter.notifyDataSetChanged();

                // Calcular y mostrar el promedio de calificación después de cargar los comentarios
                float averageRating = calculateAndRoundAverageRating(commentList);
                averageRatingBar.setRating(averageRating);
                averageRateTextView.setText(String.format("%.1f", averageRating));
            }
        });
    }

    private float calculateAndRoundAverageRating(List<ComentarioEntity> comments) {
        if (comments.isEmpty()) {
            return 0f;
        }

        int totalRating = 0;
        for (ComentarioEntity comment : comments) {
            totalRating += comment.getCalificacion();
        }

        float average = (float) totalRating / comments.size();
        return Math.round(average * 2) / 2.0f;
    }
}

