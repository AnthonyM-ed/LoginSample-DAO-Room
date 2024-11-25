package com.example.loginsample.fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.example.loginsample.Comment;
import com.example.loginsample.CommentAdapter;
import com.example.loginsample.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommentsSectionFragment extends Fragment {
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText commentInput;
    private RatingBar ratingBar;
    private RatingBar averageRatingBar;
    private TextView averageRateTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments_section, container, false);

        // Inicializar componentes
        commentInput = view.findViewById(R.id.comment_input);
        ratingBar = view.findViewById(R.id.rating_bar);
        averageRatingBar = view.findViewById(R.id.average_rating_bar);  // Inicialización del RatingBar de promedio
        averageRateTextView = view.findViewById(R.id.average_rate_text_view);  // Inicialización del TextView de promedio
        Button submitCommentButton = view.findViewById(R.id.submit_comment_button);
        commentsRecyclerView = view.findViewById(R.id.comments_recycler_view);
        Button btnBackToDetail = view.findViewById(R.id.btn_back_to_detail);

        // Configuración del RecyclerView para comentarios
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(commentAdapter);

        // Cargar los comentarios desde el archivo
        loadCommentsFromFile();

        // Calcular y mostrar el promedio de calificación después de cargar los comentarios
        float averageRating = calculateAndRoundAverageRating(commentList);
        averageRatingBar.setRating(averageRating);  // Actualizar el RatingBar
        averageRateTextView.setText(String.format("%.1f", averageRating));  // Actualizar el texto

        // Funcionalidad para agregar un nuevo comentario
        submitCommentButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString();
            int rating = (int) ratingBar.getRating();

            // Asegurarse de que el comentario no esté vacío y tenga una calificación válida
            if (!commentText.isEmpty() && rating > 0) {
                String username = "admin"; // Aquí deberías obtener el nombre del usuario logueado

                // Crear el comentario y agregarlo a la lista
                Comment newComment = new Comment(username, commentText, rating);
                commentList.add(newComment);
                commentAdapter.notifyItemInserted(commentList.size() - 1);

                // Guardar el comentario en el archivo
                saveCommentToFile(username, commentText, rating);

                // Limpiar los campos de entrada
                commentInput.setText("");
                ratingBar.setRating(0); // Restablecer la calificación

                // Recalcular el promedio después de agregar el nuevo comentario
                float updatedAverageRating = calculateAndRoundAverageRating(commentList);
                averageRatingBar.setRating(updatedAverageRating);
                averageRateTextView.setText(String.format("%.1f", updatedAverageRating));
            } else {
                Toast.makeText(getContext(), "Por favor, ingresa un comentario y calificación válida.", Toast.LENGTH_SHORT).show();
            }
        });

        // Regresa al fragmento anterior
        btnBackToDetail.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    // Método para guardar el comentario en el archivo
    private void saveCommentToFile(String username, String commentText, int rating) {
        try {
            // Obtener acceso al archivo comments.txt
            File file = new File(getContext().getFilesDir(), "comments.txt");
            FileWriter fileWriter = new FileWriter(file, true);  // 'true' para agregar al final
            BufferedWriter writer = new BufferedWriter(fileWriter);

            // Escribir el comentario en el archivo, separado por ";"
            writer.write(username + ";" + commentText + ";" + rating);
            writer.newLine();

            writer.close();
        } catch (IOException e) {
            Log.e("DetailFragment", "Error al guardar el comentario", e);
            Toast.makeText(getContext(), "Error al guardar el comentario", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCommentsFromFile() {
        try {
            // Acceder al archivo en 'assets' usando getAssets()
            InputStream inputStream = getContext().getAssets().open("comments.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Ahora usamos ";" como delimitador
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    String username = parts[0].trim();  // Nombre de usuario
                    String text = parts[1].trim();      // Texto del comentario
                    int rating = Integer.parseInt(parts[2].trim());  // Calificación

                    // Crear el comentario y agregarlo a la lista
                    Comment comment = new Comment(username, text, rating);
                    commentList.add(comment);
                }
            }
            commentAdapter.notifyDataSetChanged();  // Actualiza el RecyclerView
            reader.close();  // Cierra el BufferedReader
        } catch (IOException e) {
            Log.e("DetailFragment", "Error al leer el archivo de comentarios", e);
            Toast.makeText(getContext(), "Error al cargar comentarios", Toast.LENGTH_SHORT).show();
        }
    }

    private float calculateAndRoundAverageRating(List<Comment> comments) {
        if (comments.isEmpty()) {
            return 0f;  // Si no hay comentarios, el promedio es 0
        }

        int totalRating = 0;
        for (Comment comment : comments) {
            totalRating += comment.getRating();
        }

        float average = (float) totalRating / comments.size();  // Calcula el promedio
        return Math.round(average * 2) / 2.0f;  // Redondear a 0.5 más cercano
    }
}
