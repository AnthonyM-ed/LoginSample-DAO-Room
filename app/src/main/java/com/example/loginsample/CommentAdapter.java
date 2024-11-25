package com.example.loginsample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginsample.model.database.UserRepository;
import com.example.loginsample.model.ent.ComentarioEntity;
import com.example.loginsample.model.ent.UsuarioEntity;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<ComentarioEntity> commentList;
    private UserRepository userRepository;  // Repositorio para obtener el username

    // Constructor del adaptador con el repositorio
    public CommentAdapter(List<ComentarioEntity> commentList, UserRepository userRepository) {
        this.commentList = commentList;
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        ComentarioEntity comment = commentList.get(position);

        // Obtener el username asincrónicamente usando el repositorio
        userRepository.getUsuarioById(comment.getIdUser(), new UserRepository.GetUserCallback() {
            @Override
            public void onUserLoaded(UsuarioEntity usuario) {
                // Una vez que el usuario es cargado, actualizamos la UI en el hilo principal
                if (usuario != null) {
                    holder.usernameTextView.setText(usuario.getUserName());  // Establecer el username
                }
            }
        });

        holder.textTextView.setText(comment.getComentario());  // Establecer el comentario

        // Aquí asignamos el icono de estrella y la calificación
        holder.starImageView.setImageResource(R.drawable.fullstar);  // Icono de estrella llena
        holder.ratingTextView.setText("(" + comment.getCalificacion() + ")");
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView textTextView;
        ImageView starImageView;
        TextView ratingTextView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.comment_username);
            textTextView = itemView.findViewById(R.id.comment_text);
            starImageView = itemView.findViewById(R.id.comment_star_icon);
            ratingTextView = itemView.findViewById(R.id.comment_rating);
        }
    }
}
